package kbcp.scheduler;

import kbcp.common.base.AbstractVO;
import kbcp.common.util.ExceptionUtil;
import kbcp.common.util.LogUtil;
import kbcp.common.util.VoUtil;
import kbcp.scheduler.vo.IssueCouponListVO;
import kbcp.scheduler.vo.RunCntVO;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.CouponInfoVO;
import kbcp.site.kb.coupon.vo.ReqGetCoupon;
import kbcp.zlgoon.vo.ErrCodeMsgVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "batchLog")
@RequiredArgsConstructor
@Service
public class ScheduledTaskService {
	@Value("${app.scheduler.cron.delete-logfolder}")
	private int deleteLogFolderDay;

	@Value("${app.logging.path}")
	private String logPath;

	@Resource(name = "scheduledMapper")
	private ScheduledMapper scheduledMapper;

	private final KbCouponService couponService;

	// 로그폴더 정리
	public void cleanLogFolder() throws Exception {
		// 지정된 기간만큼의 과거날짜를 계산한다.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -deleteLogFolderDay); // 지정된 날짜가 지난 폴더
		Date date = new Date(cal.getTimeInMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String delDate = dateFormat.format(date);

		try {
			File file = new File(logPath);
			// 폴더 존재여부 확인
			if (!file.exists()) {
				log.error("[{}] log folder not found!!", logPath);
				return;
			}

			//파일이 디렉토리인지 확인
			if (!file.isDirectory()) {
				log.error("[{}] log path not folder!!", logPath);
				return;
			}

			// 날짜별 폴더들을 읽는다.
			String dirName;
			File[] logDirs = file.listFiles();
			for (File logDir : logDirs) {
				if (!logDir.isDirectory()) {
					continue;
				}

				// 날짜폴더가 아니면 skip
				dirName = logDir.getName();
				if(dirName.length() != 8 && !StringUtils.isNumeric(dirName)) {
					log.warn("[{}] dirName is not date string.", dirName);
					continue;
				}

				// 폴더날짜가 기준날짜보다 작으면 지운다.
				if(dirName.compareToIgnoreCase(delDate) < 0) {
					File[] delFiles = logDir.listFiles();
					for (File delFile : delFiles) {
						if(!delFile.delete()) {
							log.error("[{}] log file delete fail.", delFile.getAbsolutePath());
							continue;
						}
					}

					// 폴더도 삭제
					if(!logDir.delete()) {
						log.error("[{}] log dir delete fail.", logDir.getAbsolutePath());
						continue;
					}

					log.info("[{}] folder deleted.", logDir.getAbsolutePath());
				}

			}
		} catch (Exception e) {
			logException(e);
		} finally {

		}
	}

	// 당첨권 기간만료 쿠폰 발급
	public int issueCoupon(RunCntVO runCntVO) throws Exception {
		List<IssueCouponListVO> listCoupon = null;
		try {
			// 강제 쿠폰 발급 대상 리스트 조회(쿠폰 미발급 2일 경과)
			listCoupon = scheduledMapper.getIssueCouponList();
			if(listCoupon == null) {
				log.info("getIssueCouponList() no data.");
				return 0;
			}
		} catch (Exception e) {
			LogUtil.logException(e, runCntVO);
			return 0;
		}

		CouponInfoVO couponInfoVO = new CouponInfoVO();
		ReqGetCoupon reqData = new ReqGetCoupon();
		for(IssueCouponListVO coupon : listCoupon) {
			try {
				runCntVO.addTotal();

				if(!"Y".equalsIgnoreCase(coupon.getDefaultYn()) ) {
					runCntVO.addFail();
					log.error("no default goods.\n{}", VoUtil.toJson(coupon));
					continue;
				}

				couponInfoVO.setPrizeSeq(coupon.getPrizeSeq());
				couponInfoVO.setCorpCode(coupon.getCorpCode());
				couponInfoVO.setEventId(coupon.getEventId());

				reqData.setGoodsSeq(coupon.getGoodsSeq());
				reqData.setGoodsId(coupon.getGoodsId());
				reqData.setRunBatch("1");	// 배치발급

				// 쿠폰 발급
				ErrCodeMsgVO errCodeMsgVO = couponService.issueCoupon(couponInfoVO, reqData);
				if(errCodeMsgVO != null) {
					// 에러시 배치가 두번 돌지 않도록 run_batch 상태를 '1'로 바꿔준다.
					int nRet = scheduledMapper.updateRunBatch(coupon.getPrizeSeq());
					if(nRet <= 0) {
						log.error("updateRunBatch fail. [{}]\n{}\n{}",
								nRet, VoUtil.toJson(couponInfoVO), VoUtil.toJson(reqData));
					}
					runCntVO.addFail();
					log.error("Zlgoon error return.\n{}\n{}", VoUtil.toJson(errCodeMsgVO), VoUtil.toJson(coupon));
					continue;
				}

				runCntVO.addSuccess();
			} catch (Exception e) {
				runCntVO.addFail();
				logException(e, coupon, runCntVO);
				continue;
			}
		}

		// 조회된 값이 100이면 처리대상이 더 있을 수 있다.
		return listCoupon.size();
	}

	// Exception, HttpServletRequest, AbstractVO 클래스를 에러 로그로 남긴다.
	public static void logException(Object... arg) {
		StringBuilder sb = new StringBuilder();

		// HttpServletRequest, Exception, AbstractVO 는 별도의 로그형식으로 작성한다.
		for(int i=0; i<arg.length; i++) {
			if(arg[i] instanceof Exception) {
				Exception e = (Exception)arg[i];
				sb.append(ExceptionUtil.getStackTrace(e));
			} else if(arg[i] instanceof HttpServletRequest) {
				HttpServletRequest request = (HttpServletRequest)arg[i];
				String url = request.getRequestURI();
				Map<String, String[]> reqMap = request.getParameterMap();
				sb.append("\n[URL] : ").append(url);
				sb.append("\n[Parameter] ");
				if( reqMap != null && !reqMap.isEmpty()) {
					sb.append("\n").append(VoUtil.toJsonFromMapStringArray(reqMap));
				} else {
					sb.append(": None!!");
				}
			} else if(arg[i] instanceof AbstractVO) {
				AbstractVO vo = (AbstractVO)arg[i];
				sb.append("\n[VO] ");
				sb.append(arg[i].getClass().getName());
				sb.append("\n").append(VoUtil.toJson(vo));
			}
		}

		log.error(sb.toString());
	}
}
