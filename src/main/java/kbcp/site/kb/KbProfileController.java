package kbcp.site.kb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.common.vo.ResultVO;
import kbcp.site.kb.api.service.KbApiService;
import kbcp.site.kb.api.vo.*;
import kbcp.site.kb.constant.KbConstant;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.*;
import kbcp.svc.vo.EventVO;
import kbcp.svc.vo.PrizeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

// 프로파일 "prod"에서는 작동하면 안되므로 항상 "!prod" 이어야 한다.
@Profile("!prod")
@Slf4j
@RequestMapping("/api/kb")
@RequiredArgsConstructor
@Controller
public class KbProfileController extends AbstractController {
    @Value("${app.test.detail-json-filename}")
    private String detailJsonFileName;

    private final KbCouponService kbCouponService;
    private final KbApiService apiService;

    @RequestMapping(value="/prize-test.ux")
    public ModelAndView selectCouponTest(HttpServletRequest request, HttpServletResponse response, KbReqSelectPrizeVO reqTextVO) {
        log.info("prize-test.ux start.\n{}", VoUtil.toJson(reqTextVO));

        ModelAndView mv = new ModelAndView();
        mv.setViewName(pageLogout);

        try{
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqTextVO);
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
                LogUtil.logError("invalid status. [{}]", reqTextVO.getGoods_id_no(), request, reqTextVO, selPrizeVO);
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
            LogUtil.logException(e, request);
            return mv;
        }

    }

    /***** 테스트 코드 반드시 주석처리 *****/
    @RequestMapping(value="/coupon-test.ux")
    public ModelAndView couponBoxTest(HttpServletRequest request, HttpServletResponse response, KbReqCouponBoxVO reqTextVO){
        log.info("coupon-test.ux start.\n{}", VoUtil.toJson(reqTextVO));

        ModelAndView mv = new ModelAndView();
        mv.setViewName(pageLogout);

        try{
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqTextVO);
                return mv;
            }

            // 회원번호 체크
            if(StringUtil.isNull(reqTextVO.getCustomer_id())) {
                LogUtil.logError("customer_id empty.", request, reqTextVO);
                return mv;
            }

            // 고객사 식별ID 체크
            if(StringUtil.isNull(reqTextVO.getCorp_code())) {
                LogUtil.logError("corp_code empty.", request, reqTextVO);
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
            LogUtil.logException(e, request, reqTextVO);
            return mv;
        }
    }

    @PostMapping(value = "/encodeSetPrizeKB.do")
    @ResponseBody
    public ResultVO encodeSetPrizeKB(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "tran_id") String tranId,
                                     @RequestParam(value = "customer_id") String customerId,
                                     @RequestParam(value = "corp_code") String corpCode,
                                     @RequestParam(value = "req_code") String reqCode,
                                     @RequestParam(value = "goods_id") String goodsId,
                                     @RequestParam(value = "goods_id_no") String goodsIdNo )
    {
        ResultVO result = new ResultVO();

        try {
            KbReqSetPrizeVO reqVO = new KbReqSetPrizeVO();
            reqVO.setTran_id(tranId);
            reqVO.setCustomer_id(customerId);
            reqVO.setCorp_code(corpCode);
            reqVO.setReq_code(reqCode);
            reqVO.setGoods_id(goodsId);
            reqVO.setGoods_id_no(goodsIdNo);

            String strVO = VoUtil.toJson(reqVO, false);

            log.info("intput: [{}]", strVO);

            String strEncoded = apiService.encodeKbCrypto(strVO);
            result.setRsltMsge(strEncoded);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/decodeSetPrizeKB.do")
    @ResponseBody
    public ResultVO decodeSetPrizeKB(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody KbReqVO reqData )
    {
        ResultVO result = new ResultVO();

        try {

            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(reqData.getResult_text());
            if(strText == null) {
                LogUtil.logError("invalid decoded text. [{}]", request, reqData);
                result.setErrorCode(AppErrorCode.KB_INPUT_DATA);
                return result;
            }

            KbReqSetPrizeVO reqTextVO = VoUtil.fromJson(strText, KbReqSetPrizeVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqData, strText);
                result.setErrorCode(AppErrorCode.KB_INPUT_DATA);
            }


            String strVO = VoUtil.toJson(reqTextVO, false);

            log.info("intput: [{}]", strVO);

            result.setRsltMsge(strVO);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/encodeCouponInfoList.do")
    @ResponseBody
    public ResultVO encodeCouponInfoList(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(value = "tran_id") String tranId,
                                         @RequestParam(value = "customer_id") String customerId,
                                         @RequestParam(value = "corp_code") String corpCode,
                                         @RequestParam(value = "start_day") String startDay,
                                         @RequestParam(value = "end_day") String endDay )
    {
        ResultVO result = new ResultVO();

        try {
            KbReqCouponInfoVO reqVO = new KbReqCouponInfoVO();
            reqVO.setTran_id(tranId);
            reqVO.setCustomer_id(customerId);
            reqVO.setCorp_code(corpCode);
            reqVO.setStart_day(startDay);
            reqVO.setEnd_day(endDay);

            String strVO = VoUtil.toJson(reqVO, false);

            log.info("intput: [{}]", strVO);

            String strEncoded = apiService.encodeKbCrypto(strVO);
            result.setRsltMsge(strEncoded);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/decodeCouponInfoList.do")
    @ResponseBody
    public ResultVO decodeCouponInfoList(HttpServletRequest request, HttpServletResponse response,
                                         @RequestBody KbReqVO reqData )
    {
        ResultVO result = new ResultVO();

        try {

            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(reqData.getResult_text());
            if(strText == null) {
                LogUtil.logError("invalid decoded text. [{}]", request, reqData);
                result.setErrorCode(AppErrorCode.KB_INPUT_DATA);
                return result;
            }

            KbReqCouponInfoVO reqTextVO = VoUtil.fromJson(strText, KbReqCouponInfoVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqData, strText);
                result.setErrorCode(AppErrorCode.KB_INPUT_DATA);
            }

            String strVO = VoUtil.toJson(reqTextVO, false);

            log.info("intput: [{}]", strVO);

            result.setRsltMsge(strVO);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/encodeUxPrizeKB.do")
    @ResponseBody
    public ResultVO encodeUxPrizeKB(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "tran_id") String tranId,
                                    @RequestParam(value = "customer_id") String customerId,
                                    @RequestParam(value = "corp_code") String corpCode,
                                    @RequestParam(value = "goods_id") String goodsId,
                                    @RequestParam(value = "goods_id_no") String goodsIdNo )
    {
        ResultVO result = new ResultVO();

        try {
            KbReqSelectPrizeVO reqVO = new KbReqSelectPrizeVO();
            reqVO.setTran_id(tranId);
            reqVO.setCustomer_id(customerId);
            reqVO.setCorp_code(corpCode);
            reqVO.setGoods_id(goodsId);
            reqVO.setGoods_id_no(goodsIdNo);

            String strVO = VoUtil.toJson(reqVO, false);

            log.info("intput: [{}]", strVO);

            String strEncoded = apiService.encodeKbCrypto(strVO);
            String encodedURL = URLEncoder.encode(strEncoded, StandardCharsets.UTF_8.toString());
            result.setRsltMsge(encodedURL);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/encodeUxCouponKB.do")
    @ResponseBody
    public ResultVO encodeUxCouponKB(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "tran_id") String tranId,
                                     @RequestParam(value = "customer_id") String customerId,
                                     @RequestParam(value = "corp_code") String corpCode)
    {
        ResultVO result = new ResultVO();

        try {
            KbReqCouponBoxVO reqVO = new KbReqCouponBoxVO();
            reqVO.setTran_id(tranId);
            reqVO.setCustomer_id(customerId);
            reqVO.setCorp_code(corpCode);

            String strVO = VoUtil.toJson(reqVO, false);

            log.info("intput: [{}]", strVO);

            String strEncoded = apiService.encodeKbCrypto(strVO);
            String encodedURL = URLEncoder.encode(strEncoded, StandardCharsets.UTF_8.toString());
            result.setRsltMsge(encodedURL);
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    @PostMapping(value = "/encodeCouponInfo.do")
    @ResponseBody
    public ResultVO encodeCouponInfo(HttpServletRequest request, HttpServletResponse response, CouponInfoVO couponInfoVO) {
        ResultVO result = new ResultVO();

        try {
            log.info("intput: [{}]", VoUtil.toString(couponInfoVO));

            String endodedMsg = kbCouponService.encodeCouponInfo(couponInfoVO);
            result.setRsltMsge(endodedMsg);
        } catch (Exception ex) {
            LogUtil.logException(ex, request, couponInfoVO);
            result.setErrorCode(AppErrorCode.COUPON_INFO);
            return result;
        }

        return result;
    }

    // 쿠폰함 상세 페이지 이동 api
    @GetMapping(value = "/couponDetail-test.ux", consumes = {MediaType.ALL_VALUE})
    public ModelAndView moveCouponDetailPage(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/kb/coupon/couponDetail"); // 쿠폰 히스토리 페이지 세팅

        try {
            RspGetCouponDetail rspDetail = readDetailFile();

            mav.addObject("couponInfo", "aaa");
            mav.addObject("detailInfo", rspDetail);
            return mav;
        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            mav.addObject("errMsg", AppErrorCode.DETAIL_SELECT_FAIL.getErrMsg()); // 에러 페이지 전달 데이터 세팅
            mav.setViewName(errorPage); // 에러 페이지 세팅
            return mav;
        }
    }

    private RspGetCouponDetail readDetailFile() throws IOException, ParseException {
        RspGetCouponDetail rspData = null;

        JSONParser parser = new JSONParser();         // JSON 파일 읽기
        Reader reader = new FileReader(detailJsonFileName);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        ObjectMapper mapObject = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        rspData = mapObject.readValue(jsonObject.toString(), RspGetCouponDetail.class);

        rspData.setModDate("2024.12.01");
        rspData.setRunBatch("0");
        rspData.setStatus("1");
        rspData.setBacodeYn("Y");
        rspData.setBrandName("빽다방");
        rspData.setGoodsName("[빽다방] 아메리카노(HOT)");
        rspData.setBeforeBrand("");
        rspData.setBeforeGoods("");

        return rspData;
    }

    // 쿠폰 선택 페이지 - KB에서 호출
    @RequestMapping(value="/testParam.ux")
    public ModelAndView testParam(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "result_text") String resultText) {
//                                  @RequestBody String resultText) {

        log.info("testParam.ux start.\n{}", resultText);

        ModelAndView mv = new ModelAndView();
        mv.setViewName(pageLogout);

        mv.addObject("received", resultText);
        try {

            // 암호화된 데이터 복호화
            String decodedURL = URLDecoder.decode(resultText, StandardCharsets.UTF_8.toString());
            String strText = apiService.decodeKbCrypto(decodedURL);
            if(strText == null) {
                // URL decode 안된 스트링을 다시한번 시도해본다.
                strText = apiService.decodeKbCrypto(resultText);
                if(strText == null) {
                    LogUtil.logError("invalid decoded text. [{}]", request, resultText);
                    mv.addObject("error", "decode fail: " + decodedURL);
                    return mv;
                }
            }

            mv.addObject("error", "success: " + strText);
        } catch (Exception ex) {
            LogUtil.logException(ex, request, resultText);
            mv.addObject("error", ex.toString());
            return mv;
        }

        return mv;
    }

}
