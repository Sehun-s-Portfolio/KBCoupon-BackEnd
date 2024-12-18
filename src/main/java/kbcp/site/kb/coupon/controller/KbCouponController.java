package kbcp.site.kb.coupon.controller;

import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.EstimateUtil;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.common.vo.ResultVO;
import kbcp.scheduler.ScheduledTaskService;
import kbcp.scheduler.vo.RunCntVO;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.*;
import kbcp.svc.service.GoodsService;
import kbcp.zlgoon.vo.ErrCodeMsgVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import kbcp.common.base.AbstractController;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/page/kb/coupon")
public class KbCouponController extends AbstractController  {
    private final KbCouponService couponService;
    private final ScheduledTaskService scheduledService;

    // rest api 결과값 전송
    private ResultVO rspRestApi(AppErrorCode errCode) {
        ResultVO rspVO = new ResultVO();
        rspVO.setErrorCode(errCode);
        return rspVO;
    }

    // 쿠폰 발급
    @PostMapping(value = "/getCoupon.do")
    @ResponseBody
    public ResultVO getCoupon(HttpServletRequest request, HttpServletResponse response, @RequestBody ReqGetCoupon reqData) {
        log.info("getCoupon.do start.");

        ResultVO result = new ResultVO();
        CouponInfoVO couponInfoVO = null;

        try {
            // 쿠폰정보 복호화
            couponInfoVO = couponService.decodeCouponInfo(reqData.getCouponInfo());
            if(couponInfoVO == null) {
                LogUtil.logError("decodeCouponInfo empty.", request, reqData);
                return rspRestApi(AppErrorCode.COUPON_INFO);
            }

            if(StringUtil.isNull(couponInfoVO.getPrizeSeq())) {
                LogUtil.logError("prizeSeq empty.", request, reqData, couponInfoVO);
                return rspRestApi(AppErrorCode.COUPON_INFO);
            }
            if(StringUtil.isNull(couponInfoVO.getCorpCode())) {
                LogUtil.logError("corpCode empty.", request, reqData, couponInfoVO);
                return rspRestApi(AppErrorCode.COUPON_INFO);
            }
            if(StringUtil.isNull(couponInfoVO.getEventId())) {
                LogUtil.logError("eventId empty.", request, reqData, couponInfoVO);
                return rspRestApi(AppErrorCode.COUPON_INFO);
            }

            // 쿠폰 발급
            ErrCodeMsgVO errCodeMsgVO = couponService.issueCoupon(couponInfoVO, reqData);
            if(errCodeMsgVO != null) {
                LogUtil.logError("Zlgoon error return.", errCodeMsgVO, request, couponInfoVO, reqData);
                ResultVO rspVO = new ResultVO();
                rspVO.setRsltYn("N");
                rspVO.setRsltErrCode(errCodeMsgVO.getErrCode());
                rspVO.setRsltMsge(errCodeMsgVO.getErrMsg());
                return rspVO;
            }
        } catch (Exception e) {
            // 로그 많이 나와서 주석처리함.
            // LogUtil.logException(e, request, couponInfoVO, reqData);
            result.setException(e);
        }

        return result;
    }

    // 배치실행
    @PostMapping(value = "/batchJob.do")
    @ResponseBody
    public ResultVO batchJob(HttpServletRequest request, HttpServletResponse response, @RequestBody ReqGetCoupon reqData) {
        log.info("batchJob.do start.");
        ResultVO result = new ResultVO();
        EstimateUtil eu = new EstimateUtil();
        RunCntVO runCntVO = new RunCntVO();

        try {
            eu.setCheckPoint("issueCoupon.start");
            int nRet = scheduledService.issueCoupon(runCntVO);
            eu.setCheckPoint("issueCoupon.finish");
        } catch (Exception e) {
            LogUtil.logException(e);
            result.setRsltYn("N");
            result.setRsltMsge(e.toString());
            return result;
        } finally {
            log.info("issueCoupon : {}ms elapsed.",
                    eu.estimate("issueCoupon.start", "issueCoupon.finish"));
            log.info("issueCoupon end.\n{}",
                    VoUtil.toJson(runCntVO));
        }

        return result;
    }

    // 쿠폰 상세조회
    /* 사용안함
    @RequestMapping(value = "/getCouponDetail.ux")
    public ModelAndView getCouponDetail(HttpServletRequest request, HttpServletResponse response, ReqGetCouponDetail reqData) {
        ModelAndView mv = new ModelAndView();
        CouponInfoVO couponInfoVO = null;

        try {
            // 쿠폰정보 복호화
            couponInfoVO = couponService.decodeCouponInfo(reqData.getCouponInfo());
            if(couponInfoVO == null) {
                LogUtil.logError("decodeCouponInfo empty.", request, reqData);
                mv.setViewName(errorPage);
                return mv;
            }

            // 고객사 식별코드
            if(StringUtil.isNull(couponInfoVO.getCorpCode())) {
                LogUtil.logError("corp_code empty.", request, reqData);
                mv.setViewName(errorPage);
                return mv;
            }
            // 프로모션 ID
            if(StringUtil.isNull(reqData.getEventId())) {
                LogUtil.logError("event_id empty.", request, reqData);
                mv.setViewName(errorPage);
                return mv;
            }
            // tr
            if(StringUtil.isNull(reqData.getTrId())) {
                LogUtil.logError("tr_id empty.", request, reqData);
                mv.setViewName(errorPage);
                return mv;
            }

            // 쿠폰 상세조회
            RspGetCouponDetail rspVO = couponService.getCouponDetail(couponInfoVO, reqData);
            mv.addObject(rspVO);
        } catch (Exception ex) {
            LogUtil.logException(ex, request, couponInfoVO, reqData);
            mv.setViewName(errorPage);
            return mv;
        }

        mv.setViewName("/kb/coupon/couponDetail"); // 쿠폰 상세 페이지 세팅
        return mv;
    }
     */

}
