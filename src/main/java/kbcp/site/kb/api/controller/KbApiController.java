package kbcp.site.kb.api.controller;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.common.vo.ResultVO;
import kbcp.site.kb.api.service.KbApiService;
import kbcp.site.kb.api.vo.*;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.CouponInfoVO;
import kbcp.svc.vo.PrizeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/kb")
public class KbApiController extends AbstractController  {
    private final KbApiService apiService;
    private final KbCouponService couponService;

    // rest api 결과값 전송
    private KbRspVO rspPrize(KbRspVO rspVO, String trId, AppErrorCode errCode) {
        rspVO.setTran_id(trId);
        rspVO.setResult_code(errCode.getErrCode());
        rspVO.setResult_msg(errCode.getErrMsg());
        return rspVO;
    }

    // 경품 발급(당첨권 발행)
    @PostMapping(value = "/setPrize.do")
    @ResponseBody
    public KbRspVO setPrize(HttpServletRequest request, HttpServletResponse response,
                            @RequestBody KbReqVO reqData) throws Exception {
        log.info("setPrize.do start.\n{}", VoUtil.toJson(reqData));

        PrizeVO prizeVO = new PrizeVO();
        prizeVO.setTranId("0");
        KbRspVO rspData = new KbRspVO();

        try {
            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(reqData.getResult_text());
            if(strText == null) {
                LogUtil.logError("invalid decoded text. [{}]", request, reqData, reqData.getResult_text());
                return rspPrize(rspData, "0", AppErrorCode.KB_INPUT_DATA);
            }

            KbReqSetPrizeVO reqTextVO = VoUtil.fromJson(strText, KbReqSetPrizeVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqData, strText);
                return rspPrize(rspData, "0", AppErrorCode.KB_INPUT_DATA);
            }

            /////
            // 데이터 추출

            // client_tr
            String strBuf = reqTextVO.getTran_id();
            if(StringUtil.isNull(strBuf)) {
                LogUtil.logError("tran_id empty.", request, reqTextVO);
                return rspPrize(rspData, "0", AppErrorCode.KB_INPUT_DATA);
            }
            prizeVO.setTranId(strBuf);

            // 회원ID
            strBuf = reqTextVO.getCustomer_id();
            if(StringUtil.isNull(strBuf)) {
                LogUtil.logError("customer_id empty.", request, reqTextVO);
                return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INPUT_DATA);
            }
            prizeVO.setCustomerId(strBuf);

            // 고객사 식별ID
            strBuf = reqTextVO.getCorp_code();
            if(StringUtil.isNull(strBuf)) {
                LogUtil.logError("corp_code empty.", request, reqTextVO);
                return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INPUT_DATA);
            }
            prizeVO.setCorpCode(strBuf);

            // 상품 ID: 고객사 이벤트 코드(5) + 상품ID(10)
            strBuf = reqTextVO.getGoods_id();
            if(StringUtil.isNull(strBuf) || strBuf.length() < 6) {
                LogUtil.logError("goods_id empty.", request, reqTextVO);
                return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INPUT_DATA);
            }
            prizeVO.setGoodsId(strBuf);

            // 상품 ID 순번(KB의 유니크한 상품 키)
            strBuf = reqTextVO.getGoods_id_no();
            if(StringUtil.isNull(strBuf)) {
                LogUtil.logError("goods_id_no empty.", request, reqData);
                return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INPUT_DATA);
            }
            prizeVO.setGoodsIdNo(strBuf);

            // 재발급 요청 처리.
            prizeVO.setReqCode(reqTextVO.getReq_code());
            if(prizeVO.getReqCode().equals("20")) {
                // 쿠폰 정보를 조회한다.
                PrizeVO selPrizeVO = couponService.getPrizeByGoodsIdNo(prizeVO.getGoodsIdNo());
                // 이미 있다면 성공처리한다.
                if(selPrizeVO != null) {
                    log.error("already insert ok.\n{}\n{}", VoUtil.toJson(selPrizeVO), VoUtil.toJson(prizeVO));
                    return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_SUCCESS);
                }
            }

            int nRet = apiService.insertPrize(prizeVO);
            if(nRet <= 0) {
                LogUtil.logError("insert error.", request, reqData, prizeVO);
                return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INSERT_PRIZE);
            }
        } catch (Exception e) {
            LogUtil.logException(e, request, reqData, prizeVO);
            if( e instanceof AppException) {
                return rspPrize(rspData, prizeVO.getTranId(), ((AppException) e).getEnumError());
            }

            return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_INSERT_PRIZE);
        }

        return rspPrize(rspData, prizeVO.getTranId(), AppErrorCode.KB_SUCCESS);
    }

    // 쿠폰 사용현황 조회
    @PostMapping(value = "/getCouponInfoList.do")
    @ResponseBody
    public KbRspCouponInfoListVO getCouponInfoList(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestBody KbReqVO reqData) throws Exception {
        log.info("getCouponInfoList.do start.\n{}", VoUtil.toJson(reqData));

        KbReqCouponInfoVO reqTextVO = new KbReqCouponInfoVO();
        reqTextVO.setTran_id("0");
        KbRspCouponInfoListVO rspData = new KbRspCouponInfoListVO();
        rspData.setArray_size("0");

        try {
            // 암호화된 데이터 복호화
            String strText = apiService.decodeKbCrypto(reqData.getResult_text());
            if(strText == null) {
                LogUtil.logError("invalid decoded text. [{}]", request, reqData, reqData.getResult_text());
                return (KbRspCouponInfoListVO) rspPrize(rspData, "0", AppErrorCode.KB_INPUT_DATA);
            }

            reqTextVO = VoUtil.fromJson(strText, KbReqCouponInfoVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded VO. [{}]", request, reqData, strText);
                return (KbRspCouponInfoListVO) rspPrize(rspData, "0", AppErrorCode.KB_INPUT_DATA);
            }

            /////
            // 데이터 추출

            // tr
            if(StringUtil.isNull(reqTextVO.getTran_id())) {
                LogUtil.logError("tran_id empty.", request, reqTextVO);
                return (KbRspCouponInfoListVO) rspPrize(rspData,"0", AppErrorCode.KB_INPUT_DATA);
            }

            // 회원ID
            if(StringUtil.isNull(reqTextVO.getCustomer_id())) {
                LogUtil.logError("customer_id empty.", request, reqTextVO);
                return (KbRspCouponInfoListVO) rspPrize(rspData, reqTextVO.getTran_id(), AppErrorCode.KB_INPUT_DATA);
            }

            // 고객사 식별ID
            if(StringUtil.isNull(reqTextVO.getCorp_code())) {
                LogUtil.logError("corp_code empty.", request, reqTextVO);
                return (KbRspCouponInfoListVO) rspPrize(rspData, reqTextVO.getTran_id(), AppErrorCode.KB_INPUT_DATA);
            }

            // 쿠폰 내역 조회
            List<CouponInfoListVO> selData = apiService.getCouponInfoList(reqTextVO);
            if(selData == null) {
                return (KbRspCouponInfoListVO) rspPrize(rspData, reqTextVO.getTran_id(), AppErrorCode.SUCCESS);
            }

            rspPrize(rspData, reqTextVO.getTran_id(), AppErrorCode.KB_SUCCESS);
            rspData.setArray_size(Integer.toString(selData.size()));
            rspData.setArray(selData);
        } catch (Exception e) {
            LogUtil.logException(e, request, reqData);
            if( e instanceof AppException) {
                return (KbRspCouponInfoListVO) rspPrize(rspData, reqTextVO.getTran_id(), ((AppException) e).getEnumError());
            }

            return (KbRspCouponInfoListVO) rspPrize(rspData, reqTextVO.getTran_id(), AppErrorCode.KB_SELECT_FAIL);
        }

        return rspData;
    }

}
