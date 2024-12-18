package kbcp.site.kb;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.site.kb.api.service.KbApiService;
import kbcp.site.kb.api.vo.*;
import kbcp.site.kb.constant.KbConstant;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.CouponInfoVO;
import kbcp.site.kb.coupon.vo.CouponListVO;
import kbcp.site.kb.coupon.vo.GoodsListVO;
import kbcp.site.kb.coupon.vo.PrizeEventVO;
import kbcp.svc.vo.EventVO;
import kbcp.svc.vo.PrizeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequestMapping("/api/kb")
@RequiredArgsConstructor
@Controller
public class KbPageController extends AbstractController {

    private final KbCouponService kbCouponService;
    private final KbApiService apiService;

    // 쿠폰 선택 페이지 - KB에서 호출
    @RequestMapping(value="/prize.ux")
    public ModelAndView selectCoupon(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "result_text") String resultText) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mv = new ModelAndView();
        mv.setViewName(pageLogout);

        if( StringUtil.isNull(resultText) ) {
            log.error("encode text empty.");
            return mv;
        }

        log.info("prize.ux start.\n{}", resultText);

        try{
            // URLDecode
            String decodedURL = URLDecoder.decode(resultText, StandardCharsets.UTF_8.toString());
            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(decodedURL);
            if(strText == null) {
                // URLDecode 안된 스트링을 다시한번 시도해본다.
                strText = apiService.decodeKbCrypto(resultText);
                if(strText == null) {
                    LogUtil.logError("invalid decoded text. [{}]", request, decodedURL);
                    return mv;
                }
            }

            KbReqSelectPrizeVO reqTextVO = VoUtil.fromJson(strText, KbReqSelectPrizeVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, strText);
                return mv;
            }

            /////
            // 수신데이터 처리

            // 회원번호 체크
            if(StringUtil.isNull(reqTextVO.getCustomer_id())) {
                LogUtil.logError("customer_id empty.", request, reqTextVO);
                return mv;
            }

            // 회사구분 체크
            if(StringUtil.isNull(reqTextVO.getCorp_code())) {
                LogUtil.logError("corp_code empty.", request, reqTextVO);
                return mv;
            }

            // 상품코드 순번 체크
            if(StringUtil.isNull(reqTextVO.getGoods_id_no())) {
                LogUtil.logError("goods_id_no empty.", request, reqTextVO);
                return mv;
            }

            /////
            // DB 처리

            // 쿠폰 정보를 조회한다..
            PrizeVO selPrizeVO = kbCouponService.getPrizeByGoodsIdNo(reqTextVO.getGoods_id_no());

            if(selPrizeVO == null) {
                LogUtil.logError("getPrizeByGoodsIdNo() empty.", request, reqTextVO);
                return mv;
            }

            // 상태가 쿠폰 발급전이 아니면 에러처리.
            if(!selPrizeVO.getStatus().equals(KbConstant.PRIZE_STATUS_ACCEPT)) {
                // 로그 많이 남아서 주석처리함.
                // LogUtil.logError("invalid status. [{}]", reqTextVO.getGoods_id_no(), request, reqTextVO, selPrizeVO);
                return mv;
            }

            // 회사구분이 다르면 에러처리
            if(!selPrizeVO.getCorpCode().equals(reqTextVO.getCorp_code())) {
                LogUtil.logError("invalid corp_code. [{}]", reqTextVO.getGoods_id_no(), request, reqTextVO, selPrizeVO);
                return mv;
            }

            // 회원번호가 다르면 에러처리
            if(!selPrizeVO.getCustomerId().equals(reqTextVO.getCustomer_id())) {
                LogUtil.logError("invalid customer_id. [{}]", reqTextVO.getGoods_id_no(), request, reqTextVO, selPrizeVO);
                return mv;
            }

            // 이벤트 조회
            EventVO eventVO = kbCouponService.getEventByEventSeq(selPrizeVO.getEventSeq());
            if(eventVO == null) {
                LogUtil.logError("getEventByEventSeq() empty.", request, reqTextVO, selPrizeVO);
                return mv;
            }

            // 화면에 전송할 데이터로 변환
            PrizeEventVO prizeEventVO = new PrizeEventVO(eventVO);
            // 쿠폰발행에 사용할 당첨권 seq를 등록해준다.
            prizeEventVO.setPrizeSeq(selPrizeVO.getPrizeSeq());

            // 이벤트에 해당하는 상품 조회
            List<GoodsListVO> goodsData = kbCouponService.getGoodsListByEventSeq(eventVO.getEventSeq());

            if(goodsData == null) {
                LogUtil.logError("getGoodsListByEventSeq() empty.", request, eventVO);
                return mv;
            }

            // 쿠폰발급시 사용할 쿠폰정보를 암호화 해서 넘겨준다.
            CouponInfoVO couponInfoVO = new CouponInfoVO();
            couponInfoVO.setPrizeSeq(selPrizeVO.getPrizeSeq());
            couponInfoVO.setCorpCode(selPrizeVO.getCorpCode());
            couponInfoVO.setEventId(prizeEventVO.getEventId());
            couponInfoVO.setCustomerId(reqTextVO.getCustomer_id());

            String strBuf = kbCouponService.encodeCouponInfo(couponInfoVO);

            // 쿠폰정보를 암호화 해서 내려준다.
            mv.addObject("couponInfo", strBuf);
            // 이벤트 정보
            mv.addObject("eventInfo", prizeEventVO);
            // 상품 리스트
            mv.addObject("goodsList", goodsData);

            mv.setViewName("/kb/coupon/selectCoupon"); // 당첨권 쿠폰 선택 페이지
            return mv;

        } catch (Exception e){
            LogUtil.logException(e, request, resultText);
            mv.addObject("errMsg", AppErrorCode.GOODS_SELECT_FAIL.getErrMsg());
            mv.setViewName(errorPage);
            return mv;
        }

    }

    // 쿠폰함 페이지
    @RequestMapping(value="/coupon.ux")
    public ModelAndView couponBox(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "result_text") String resultText) {
        response.setHeader("Cache-Control", "no-store, no-cache");

        ModelAndView mv = new ModelAndView();
        mv.setViewName(pageLogout);

        if( StringUtil.isNull(resultText) ) {
            log.error("encode text empty.");
            return mv;
        }

        log.info("coupon.ux start.\n{}", resultText);

        try{
            // URLDecode
            String decodedURL = URLDecoder.decode(resultText, StandardCharsets.UTF_8.toString());
            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(decodedURL);
            if(strText == null) {
                // URLDecode 안된 스트링을 다시한번 시도해본다.
                strText = apiService.decodeKbCrypto(resultText);
                if(strText == null) {
                    LogUtil.logError("invalid decoded text. [{}]", request, decodedURL);
                    return mv;
                }
            }

            KbReqCouponBoxVO reqTextVO = VoUtil.fromJson(strText, KbReqCouponBoxVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, strText);
                return mv;
            }

            // 회원번호 체크
            if(StringUtil.isNull(reqTextVO.getCustomer_id())) {
                LogUtil.logError("customer_id empty.", request, strText);
                return mv;
            }

            // 고객사 식별ID 체크
            if(StringUtil.isNull(reqTextVO.getCorp_code())) {
                LogUtil.logError("corp_code empty.", request, strText);
                return mv;
            }

            // 이용가능 쿠폰함 조회
            List<CouponListVO> couponData = kbCouponService.getCouponBoxListAvail(reqTextVO);

            // 쿠폰함 조회시 사용할 쿠폰정보를 암호화 해서 넘겨준다.
            CouponInfoVO couponInfoVO = new CouponInfoVO();
            couponInfoVO.setCorpCode(reqTextVO.getCorp_code());
            couponInfoVO.setCustomerId(reqTextVO.getCustomer_id());

            // 쿠폰정보를 암호화 해서 내려준다.
            String strBuf = kbCouponService.encodeCouponInfo(couponInfoVO);
            mv.addObject("couponInfo", strBuf);
            mv.addObject("couponData", couponData);

            mv.setViewName("/kb/coupon/coupon"); // 쿠폰함 페이지
            return mv;

        } catch (Exception e){
            LogUtil.logException(e, request, resultText);
            mv.addObject("errMsg", AppErrorCode.AVAILABLE_COUPON_SELECT_FAIL.getErrMsg());
            mv.setViewName(errorPage);
            return mv;
        }
    }

}
