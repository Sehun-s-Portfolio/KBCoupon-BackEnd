package kbcp.zlgoon.controller;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.site.kb.api.service.KbApiService;
import kbcp.site.kb.api.vo.KbReqSetPrizeVO;
import kbcp.site.kb.api.vo.KbReqVO;
import kbcp.site.kb.api.vo.KbRspVO;
import kbcp.site.kb.constant.KbConstant;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.svc.vo.PrizeVO;
import kbcp.zlgoon.service.ZlgoonService;
import kbcp.zlgoon.vo.ReqExchageVO;
import kbcp.zlgoon.vo.RspExchangeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/zgn")
public class ZlgoonController extends AbstractController  {
    private final ZlgoonService zlgoonService;

    // rest api 결과값 전송
    private RspExchangeVO rspPrize(RspExchangeVO rspVO, ReqExchageVO reqData, AppErrorCode errCode) {
        rspVO.setVer(reqData.getVer());
        rspVO.setTrId(reqData.getTr_id());
        /*
        LocalDateTime now = LocalDateTime.now();
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // 포맷팅 현재 날짜/시간 출력
         */
        // 그냥 요청받은 시간을 넣자.
        rspVO.setDateTime(reqData.getDate_time());
        rspVO.setResCode(errCode.getErrCode());
        rspVO.setErrmessage(errCode.getErrMsg());
        return rspVO;
    }

    // 경품 발급(당첨권 발행)
    @PostMapping(value = "/exchange.do")
    @ResponseBody
    public RspExchangeVO exchange(HttpServletRequest request, HttpServletResponse response, @RequestBody ReqExchageVO reqData) {
        RspExchangeVO rspData = new RspExchangeVO();

        try {
            int nRet = zlgoonService.insertExchange(reqData);
            if(nRet <= 0) {
                LogUtil.logError("insert error.", request, reqData);
                return rspPrize(rspData, reqData, AppErrorCode.JGN_ERROR);
            }

            PrizeVO prizeVO = new PrizeVO();
            prizeVO.setTranId(reqData.getTr_id());
            prizeVO.setCouponNum(reqData.getBarcode_num());
            prizeVO.setCouponUseTime(reqData.getExchange_date());
            if(!StringUtil.isNull(reqData.getCoupon_status())) {
                if(reqData.getCoupon_status().equals("1")) {
                    prizeVO.setStatus(KbConstant.PRIZE_STATUS_EXHANGE); // 교환완료
                    prizeVO.setCouponStatus("01"); // 사용
                } else {
                    prizeVO.setCouponStatus("02"); // 미사용
                }
            }

            if(!StringUtil.isNull(reqData.getExchange_type())) {
                if(reqData.getExchange_type().equals("3")) {
                    prizeVO.setStatus(KbConstant.PRIZE_STATUS_CANCEL); // 교환취소
                }
            }

            nRet = zlgoonService.updatePrizeExchange(prizeVO);
            if(nRet <= 0) {
                LogUtil.logError("updatePrizeExchange error.", request, reqData, prizeVO);
                return rspPrize(rspData, reqData, AppErrorCode.JGN_ERROR);
            }
        } catch (Exception e) {
            LogUtil.logException(e, request, reqData);
            if( e instanceof AppException) {
                return rspPrize(rspData, reqData, ((AppException) e).getEnumError());
            }

            return rspPrize(rspData, reqData, AppErrorCode.JGN_ERROR);
        }

        return rspPrize(rspData, reqData, AppErrorCode.JGN_SUCCESS);
    }
}
