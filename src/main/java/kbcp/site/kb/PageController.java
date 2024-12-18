package kbcp.site.kb;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.vo.ResultObjVO;
import kbcp.site.kb.api.vo.KbReqCouponBoxVO;
import kbcp.site.kb.api.vo.KbReqVO;
import kbcp.site.kb.constant.KbConstant;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.*;
import kbcp.site.kb.faq.service.FaqService;
import kbcp.site.kb.faq.vo.FaqListVO;
import kbcp.site.kb.faq.vo.FaqReqCustomerVO;
import kbcp.site.kb.inquiry.service.InquiryService;
import kbcp.site.kb.inquiry.vo.InquiryListVO;
import kbcp.site.kb.inquiry.vo.InquiryReqCustomerVO;
import kbcp.svc.vo.EventVO;
import kbcp.svc.vo.PrizeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RequestMapping("/page/kb")
@RequiredArgsConstructor
@Controller
public class PageController extends AbstractController {

    private final KbCouponService kbCouponService;
    private final InquiryService inquiryService;
    private final FaqService faqService;

    // 복호화된 회원 및 고객사 정보 검증 예외 처리
    private String encodeDataProve(CouponInfoVO couponInfoVO) {
        // 복호화된 데이터가 존재하지 않을 시 예외 처리
        if (couponInfoVO == null) {
            return "couponInfoVO is null.";
        }

        // 회원 멤버쉽 코드 확인
        if (StringUtil.isNull(couponInfoVO.getCustomerId())) {
            return "customer_id empty.";
        }

        // 고객사 식별 ID 확인
        if (StringUtil.isNull(couponInfoVO.getCorpCode())) {
            return "corp_code empty.";
        }

        return null;
    }

    // 쿠폰 선택 페이지 - 쿠폰함에서 호출
    @PostMapping(value = "/selectCoupon.ux")
    public ModelAndView selectCoupon(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ReqSelectCoupon reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        mav.setViewName(pageLogout);

        try {
            if (StringUtil.isNull(reqData.getCouponInfo())) {
                log.error("encode text empty.");
                return mav;
            }

            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getCouponInfo());

            String customerId = null;
            if (couponInfoVO == null) {
                LogUtil.logError("decodeCouponInfo empty.", request, reqData);
                return mav;
            }

            /////
            // 수신데이터 처리

            // 상품일련번호 체크
            if (StringUtil.isNull(reqData.getGoodsIdNo())) {
                LogUtil.logError("goods_id_no empty.", request, reqData);
                return mav;
            }

            // 회사구분 체크
            if (StringUtil.isNull(couponInfoVO.getCorpCode())) {
                LogUtil.logError("corp_code empty.", request, reqData);
                return mav;
            }

            // 회원번호 체크
            if (StringUtil.isNull(couponInfoVO.getCustomerId())) {
                LogUtil.logError("customer_id empty.", request, reqData);
                return mav;
            } else {
                customerId = couponInfoVO.getCustomerId();
            }

            /////
            // DB 처리

            // 쿠폰 정보를 조회한다..
            PrizeVO selPrizeVO = kbCouponService.getPrizeByGoodsIdNo(reqData.getGoodsIdNo());
            if (selPrizeVO == null) {
                LogUtil.logError("getPrizeByGoodsIdNo() empty.", request, reqData, reqData);
                return mav;
            }

            // 상태가 쿠폰 발급전이 아니면 에러처리.
            if (!selPrizeVO.getStatus().equals(KbConstant.PRIZE_STATUS_ACCEPT)) {
                // 로그 많이 남아서 주석처리함.
                // LogUtil.logError("invalid status.", request, reqData, selPrizeVO);
                return mav;
            }

            // 회사구분이 다르면 에러처리
            if (!selPrizeVO.getCorpCode().equals(couponInfoVO.getCorpCode())) {
                LogUtil.logError("invalid corp_code. [{}]", couponInfoVO.getCorpCode(), request, reqData, selPrizeVO);
                return mav;
            }

            // 회원번호가 다르면 에러처리
            if (!selPrizeVO.getCustomerId().equals(couponInfoVO.getCustomerId())) {
                LogUtil.logError("invalid customer_id. [{}]", couponInfoVO.getCustomerId(), request, reqData, selPrizeVO);
                return mav;
            }

            // 이벤트 조회
            EventVO eventVO = kbCouponService.getEventByEventSeq(selPrizeVO.getEventSeq());
            if (eventVO == null) {
                LogUtil.logError("getEventByEventSeq() empty.", request, reqData, selPrizeVO);
                return mav;
            }

            // 화면에 전송할 데이터로 변환
            PrizeEventVO prizeEventVO = new PrizeEventVO(eventVO);
            // 쿠폰발행에 사용할 당첨권 seq를 등록해준다.
            prizeEventVO.setPrizeSeq(selPrizeVO.getPrizeSeq());

            // 이벤트에 해당하는 상품 조회
            List<GoodsListVO> goodsData = kbCouponService.getGoodsListByEventSeq(eventVO.getEventSeq());

            if (goodsData == null) {
                LogUtil.logError("getGoodsListByEventSeq() empty.", request, eventVO);
                return mav;
            }

            // 쿠폰발급시 사용할 쿠폰정보를 암호화 해서 넘겨준다.
            CouponInfoVO newCouponInfoVO = new CouponInfoVO();
            newCouponInfoVO.setPrizeSeq(selPrizeVO.getPrizeSeq());
            newCouponInfoVO.setCorpCode(selPrizeVO.getCorpCode());
            newCouponInfoVO.setEventId(prizeEventVO.getEventId());
            newCouponInfoVO.setCustomerId(customerId);

            String strBuf = kbCouponService.encodeCouponInfo(newCouponInfoVO);

            // 쿠폰정보를 암호화 해서 내려준다.
            mav.addObject("couponInfo", strBuf);
            // 이벤트 정보
            mav.addObject("eventInfo", prizeEventVO);
            // 상품 리스트
            mav.addObject("goodsList", goodsData);
            // 이전 접속 쿠폰함 페이지
            mav.addObject("prevPage", reqData.getPrevPage());

            mav.setViewName("/kb/coupon/selectCoupon"); // 당첨권 쿠폰 선택 페이지
            return mav;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.GOODS_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            return mav;
        }
    }


    // 쿠폰함 페이지 - (자체 내부 접근)
    @RequestMapping(value = "/availableCoupon.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView couponBox(HttpServletRequest request, HttpServletResponse response, @ModelAttribute KbReqVO reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        mav.setViewName(pageLogout);

        try {
            if (StringUtil.isNull(reqData.getResult_text())) {
                log.error("encode text empty.");
                return mav;
            }

            CouponInfoVO decodeInfo = kbCouponService.decodeCouponInfo(reqData.getResult_text());
            if (decodeInfo == null) {
                LogUtil.logError("invalid decoded text.");
                return mav;
            }

            // 회사구분 체크
            if (StringUtil.isNull(decodeInfo.getCorpCode())) {
                LogUtil.logError("corp_code empty.", request, reqData);
                return mav;
            }

            // 회원번호 체크
            if (StringUtil.isNull(decodeInfo.getCustomerId())) {
                LogUtil.logError("customer_id empty.", request, reqData);
                return mav;
            }


            KbReqCouponBoxVO reqTextVO = new KbReqCouponBoxVO();
            reqTextVO.setCorp_code(decodeInfo.getCorpCode());
            reqTextVO.setTran_id(decodeInfo.getPrizeSeq());
            reqTextVO.setCustomer_id(decodeInfo.getCustomerId());

            // 이용가능 쿠폰함 조회
            List<CouponListVO> couponData = kbCouponService.getCouponBoxListAvail(reqTextVO);

            // 쿠폰함 조회시 사용할 쿠폰정보를 암호화 해서 넘겨준다.
            CouponInfoVO couponInfoVO = new CouponInfoVO();
            couponInfoVO.setCorpCode(reqTextVO.getCorp_code());
            couponInfoVO.setCustomerId(reqTextVO.getCustomer_id());

            // 쿠폰정보를 암호화 해서 내려준다.
            String strBuf = kbCouponService.encodeCouponInfo(couponInfoVO);
            mav.addObject("couponInfo", strBuf);
            mav.addObject("couponData", couponData);
            mav.addObject("memberId", couponInfoVO.getCorpCode());
            mav.addObject("prevPage", "our");

            mav.setViewName("/kb/coupon/coupon"); // 쿠폰함 페이지
            return mav;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.AVAILABLE_COUPON_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }
    }


    // 쿠폰함 상세 페이지 이동 api
    @PostMapping(value = "/couponDetail.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveCouponDetailPage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CouponDetailMoveVO reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        if (StringUtil.isNull(reqData.getResult_text())) {
            log.error("encode text empty.");
            mav.setViewName(pageLogout);
            return mav;
        }

        mav.setViewName("/kb/coupon/couponDetail"); // 쿠폰 히스토리 페이지 세팅


        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResult_text());

            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);
            if (!StringUtil.isNull(strRet)) {
                log.error(strRet);
                mav.setViewName(pageLogout);
                return mav;
            }

            // 실운영 시 해제
            ReqGetCouponDetail detailData = new ReqGetCouponDetail();
            detailData.setTrId(reqData.getTran_id());
            detailData.setEventId(reqData.getEvent_id());
            RspGetCouponDetail rspDetail = kbCouponService.getCouponDetail(couponInfoVO, detailData);

            // 고객 및 회원 암호화된 인증 정보 반환
            mav.addObject("couponInfo", reqData.getResult_text());
            mav.addObject("prevPage", reqData.getPrev_page());
            mav.addObject("goodsId", reqData.getGoods_id());
            mav.addObject("goodsIdNo", reqData.getGoods_id_no());
            mav.addObject("detailInfo", rspDetail);

            return mav;
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.DETAIL_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }
    }


    // 쿠폰 히스토리 페이지 이동 api
    @PostMapping(value = "/couponHistory.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveCouponHistoryPage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CouponHistoryReqCustomerVO reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        if (StringUtil.isNull(reqData.getResultText())) {
            log.error("encode text empty.");
            mav.setViewName(pageLogout);
            return mav;
        }

        mav.setViewName("/kb/coupon/couponHistory"); // 쿠폰 히스토리 페이지 세팅

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());

            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if (!StringUtil.isNull(strRet)) {
                LogUtil.logError(strRet, request, reqData);
                mav.setViewName(pageLogout);
                return mav;
            }

            // 쿠폰 히스토리에 노출될 쿠폰 데이터 호출
            List<CouponListVO> couponHistoryList = kbCouponService.getCouponBoxListHistory(couponInfoVO);

            // View 에 이력 리스트 전달
            mav.addObject("couponHistoryList", couponHistoryList);

            // 접속 고객 멤버십 코드 반환
            mav.addObject("couponInfo", reqData.getResultText());

            return mav;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.HISTORY_COUPON_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }
    }


    // FAQ 페이지 이동 api
    @PostMapping(value = "/faq.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveFaqPage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FaqReqCustomerVO reqData) throws Exception {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        if (StringUtil.isNull(reqData.getResultText())) {
            log.error("encode text empty.");
            mav.setViewName(pageLogout);
            return mav;
        }

        mav.setViewName("/kb/faq/faq"); // FAQ 관리 페이지 세팅

        ResultObjVO<FaqListVO> result = new ResultObjVO<>();

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());

            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if (!StringUtil.isNull(strRet)) {
                LogUtil.logError(strRet, request, reqData);
                mav.setViewName(pageLogout);
                return mav;
            }

            // 전체 FAQ 리스트 호출
            List<FaqListVO> rspData = faqService.getFaqList(couponInfoVO.getCorpCode());

            // 정상적으로 호출된 FAQ 리스트를 세팅
            result.setRspList(rspData);

            // FAQ 리스트 반환
            mav.addObject("faqList", result);
            // 쿠폰을 사용하는 인증된 회원 및 고객사 정보 암호화하여 전달
            mav.addObject("couponInfo", reqData.getResultText());
            // 고객사 ID
            mav.addObject("memberId", couponInfoVO.getCorpCode());

            return mav;
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.FAQ_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }
    }


    // 상담(문의) 작성 페이지 이동 api
    @PostMapping(value = "/inquiry.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveInquiryPage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute InquiryReqCustomerVO reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        if (StringUtil.isNull(reqData.getResultText())) {
            log.error("encode text empty.");
            mav.setViewName(pageLogout);
            return mav;
        }

        mav.setViewName("/kb/inquiry/inquiry"); // 상담(문의) 이력 페이지 세팅

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());

            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if (!StringUtil.isNull(strRet)) {
                LogUtil.logError(strRet, request, reqData);
                mav.setViewName(pageLogout);
                return mav;
            }

            // 회원 및 고객사 암호화된 인증 정보 반환
            mav.addObject("couponInfo", reqData.getResultText());
            // 고객사 ID
            mav.addObject("memberId", couponInfoVO.getCorpCode());

            return mav;
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.INQUIRY_WRITE_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }

    }


    // 상담(문의) 이력 페이지 이동 api
    @PostMapping(value = "/inquiryHistory.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveInquiryHistoryPage(HttpServletRequest request, HttpServletResponse response,
                                               @ModelAttribute InquiryReqCustomerVO reqData) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mav = new ModelAndView();
        if (StringUtil.isNull(reqData.getResultText())) {
            log.error("encode text empty.");
            mav.setViewName(pageLogout);
            return mav;
        }

        mav.setViewName("/kb/inquiry/inquiryHistory"); // 상담(문의) 이력 페이지 세팅

        ResultObjVO<InquiryListVO> result = new ResultObjVO<>();

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());

            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if (!StringUtil.isNull(strRet)) {
                LogUtil.logError(strRet, request, reqData);
                mav.setViewName(pageLogout);
                return mav;
            }

            // 작성 문의 리스트 호출
            HashMap<String, Object> rspData = inquiryService.getInquiryList(couponInfoVO.getCustomerId(), 1, 1L, couponInfoVO.getCorpCode());

            // 정상적으로 호출된 문의 리스트를 세팅
            result.setRspList((List<InquiryListVO>) rspData.get("inquiryList"));

            // 문의 리스트 반환
            mav.addObject("inquiryList", result);
            // 문의 갯수 반환
            mav.addObject("inquiryCount", Long.parseLong(rspData.get("inquiryCount").toString()));
            // 고객 및 회원 암호화된 인증 정보 반환
            mav.addObject("couponInfo", reqData.getResultText());
            // 고객사 ID
            mav.addObject("memberId", couponInfoVO.getCorpCode());

            return mav;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.INQUIRY_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }

    }

    // 5분 타임아웃 시 에러 페이지 강제 이동 api
    @GetMapping(value = "/error.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveErrorPage(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache");
        ModelAndView mav = new ModelAndView();
        mav.setViewName(pageLogout);
        return mav;
    }

}
