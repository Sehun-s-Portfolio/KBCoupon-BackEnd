package kbcp.site.kb.coupon.service;


import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.site.kb.api.crypto.KbCryptoSeed;
import kbcp.site.kb.api.vo.KbReqCouponBoxVO;
import kbcp.site.kb.constant.KbConstant;
import kbcp.site.kb.coupon.mapper.KbCouponMapper;
import kbcp.site.kb.coupon.vo.*;
import kbcp.svc.AppCryptoRsa;
import kbcp.svc.vo.CouponVO;
import kbcp.svc.vo.EventVO;
import kbcp.svc.vo.GoodsVO;
import kbcp.svc.vo.PrizeVO;
import kbcp.zlgoon.HttpZlgoon;
import kbcp.zlgoon.service.ZlgoonService;
import kbcp.zlgoon.vo.ErrCodeMsgVO;
import kbcp.zlgoon.vo.RspOrderCouponHttpVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KbCouponService {

    private final KbCryptoSeed kbCryptoRsa;  // KB RSA 모듈
    private final AppCryptoRsa cryptoRsa;   // 자체 RSA 모듈
    private final HttpZlgoon httpZlgoon;    // 즐거운 연동모듈
    private final ZlgoonService zlgoonService;

    @Resource(name = "kbCouponMapper")
    private KbCouponMapper couponMapper;

    // transaction을 피하기 위해 에러 리턴.
    private ErrCodeMsgVO rtnCodeMsg(AppErrorCode appError)  {
        return new ErrCodeMsgVO(appError.getErrCode(), appError.getErrMsg());
    }

    // 쿠폰함 유저 데이터 암호화.
    public String encodeCouponInfo(CouponInfoVO couponInfoVO) throws Exception {
        String strCrypto = VoUtil.toJson(couponInfoVO, false);
        return cryptoRsa.encode(strCrypto);
    }

    // 쿠폰함 유저 데이터 복호화.
    public CouponInfoVO decodeCouponInfo(String strEncode) throws Exception {
        CouponInfoVO reqTextVO = null;

        if (StringUtil.isNull(strEncode)) {
            log.error("encode text empty.");
            return reqTextVO;
        }

        try {
            String strText = cryptoRsa.decode(strEncode);
            if (StringUtil.isNull(strText)) {
                LogUtil.logError("decoded text empty. [{}]", strEncode);
                return reqTextVO;
            }

            reqTextVO = VoUtil.fromJson(strText, CouponInfoVO.class);
            if(reqTextVO == null) {
                LogUtil.logError("invalid decoded text. [{}]", strText);
                return reqTextVO;
            }
        } catch (Exception e) {
            LogUtil.logException(e);
            return reqTextVO;
        }

        return reqTextVO;
    }

    // 쿠폰 발급
    public ErrCodeMsgVO issueCoupon(CouponInfoVO couponInfoVO, ReqGetCoupon reqData) throws Exception {
        CouponVO couponVO = new CouponVO();
        ErrCodeMsgVO errCodeMsgVO = null;
        int nRet = 0;

        // 쿠폰정보 조회
        PrizeGoodsVO prizeGoodsVO = couponMapper.getPrizeByCouponInfo(couponInfoVO);
        if(prizeGoodsVO == null) {
            LogUtil.logError("prizeGoodsVO empty.");
            throw new AppException(AppErrorCode.GET_PRIZE_INFO);
        }

        // 상품이 변경되었다면 변경된 상품의 seq로 상품정보를 조회한다.
        if(!prizeGoodsVO.getGoodsSeq().equals(reqData.getGoodsSeq())) {
            GoodsVO newGoodsVO = couponMapper.getGoodsByGoodsSeq(reqData.getGoodsSeq());
            if(newGoodsVO == null) {
                LogUtil.logError("newGoodsVO empty. {}\n{}",
                        VoUtil.toJson(reqData), VoUtil.toJson(couponInfoVO));
                throw new AppException(AppErrorCode.GET_GOODS_INFO);
            }

            // 기존의 상품정보를 새로운 상품정보로 바꿔준다.
            prizeGoodsVO.setStatusUpdateYn(newGoodsVO.getStatusUpdateYn());
            prizeGoodsVO.setExpireDate(newGoodsVO.getExpireDate());
        }


        // 0:당첨권 접수, 1:쿠폰발급완료, 2:교환완료, 3:교환취소, 4:알수없음, 9:삭제
        if(!prizeGoodsVO.getStatus().equals(KbConstant.PRIZE_STATUS_ACCEPT)) {
            throw new AppException(AppErrorCode.ALREADY_ISSUED_COUPON);
        }

        try {
            /////
            // 쿠폰발급이력을 insert
            couponVO.setCmd("0"); // 쿠폰발행
            couponVO.setPrizeSeq(couponInfoVO.getPrizeSeq());
            couponVO.setMemberId(couponInfoVO.getCorpCode());
            couponVO.setEventId(couponInfoVO.getEventId());
            couponVO.setTrId(prizeGoodsVO.getTranId());
            couponVO.setGoodsId(reqData.getGoodsId());
            couponVO.setRunBatch(reqData.getRunBatch());
            nRet = couponMapper.insertCoupon(couponVO);
            if(nRet <= 0) {
                LogUtil.logError("insertCoupon fail. [{}]", nRet, couponVO, couponInfoVO, prizeGoodsVO);
                return rtnCodeMsg(AppErrorCode.ZLGOON_HTTP_ERROR);
            }

            // 즐거운 토큰 발급요청
            RspOrderCouponHttpVO rspData = httpZlgoon.orderCoupon(couponInfoVO.getEventId(),
                    reqData.getGoodsId(),
                    couponInfoVO.getCorpCode(),
                    prizeGoodsVO.getTranId(),
                    "", "");

            /////
            // 쿠폰발급이력을 update
            couponVO.setRspCode(rspData.getRspCode());
            couponVO.setRspMsg(rspData.getRspMsg());
            couponVO.setCouponNum(rspData.getCouponNum());
            couponVO.setOrderNum(rspData.getOrderNum());
            nRet = couponMapper.updateCoupon(couponVO);
            if(nRet <= 0) {
                LogUtil.logError("updateCoupon fail. [{}]", nRet, couponVO, couponInfoVO, prizeGoodsVO);
                throw new AppException(AppErrorCode.ZLGOON_HTTP_ERROR);
            }

            // 에러리턴
            if(!rspData.getRspCode().contains("SUCCESS CODE")) {
                LogUtil.logError("http error return.", rspData, couponVO);
                /*
                errCodeMsgVO = zlgoonService.getErrCodeMsg(rspData.getRspCode());
                if(errCodeMsgVO == null) {
                    throw new AppException(AppErrorCode.ZLGOON_HTTP_ERROR);
                }
                 */
                return new ErrCodeMsgVO(rspData.getRspCode(), rspData.getRspMsg());
            }

            // 쿠폰번호가 없으면 에러처리.
            if(StringUtil.isNull(rspData.getCouponNum())) {
                LogUtil.logError("CouponNum empty.", couponInfoVO, reqData, rspData);
                throw new AppException(AppErrorCode.ZLGOON_HTTP_ERROR);
            }

            /////
            // Prize 상태를 쿠폰발급 완료로 설정한다.
            PrizeVO prizeVO = new PrizeVO();
            prizeVO.setRunBatch(reqData.getRunBatch());
            prizeVO.setPrizeSeq(couponInfoVO.getPrizeSeq());
            if(prizeGoodsVO.getStatusUpdateYn().equalsIgnoreCase("N")) {
                prizeVO.setStatus(KbConstant.PRIZE_STATUS_NO_ALARM);
            } else {
                prizeVO.setStatus(KbConstant.PRIZE_STATUS_COUPON);
            }

            // 상품이 변경되었다면 이전과 현재의 상품 seq를 변경해준다.
            if(!prizeGoodsVO.getGoodsSeq().equals(reqData.getGoodsSeq())) {
                prizeVO.setGoodsSeq(reqData.getGoodsSeq());
                prizeVO.setGoodsSeqBefore(prizeGoodsVO.getGoodsSeq());
            }

            // 쿠폰만료일을 계산한다.
            if(StringUtil.isNull(prizeGoodsVO.getExpireDate())) {
                LogUtil.logError("exprieDate empty.", couponInfoVO, reqData, prizeGoodsVO);
            } else {
                // 상품 유효기간 : 주문일로부터 (주문당일 1일 기준) 1 : 일까지 Ex ) 0|180 1|2017-08-31
                String[] arr = prizeGoodsVO.getExpireDate().split("\\|");
                int nType = StringUtil.StrToInt(arr[0], 99);
                if(0 == nType) {
                    // 0|180
                    int nDay = StringUtil.StrToInt(arr[1], 0);
                    if(nDay == 0) {
                        LogUtil.logError("invalid exprieDate.", couponInfoVO, reqData, prizeGoodsVO);
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, nDay - 1); // expireDate는 만료일이라 -1을 해 줌.
                        Date date = new Date(cal.getTimeInMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        prizeVO.setCouponExpiredDate(dateFormat.format(date));
                    }
                } else if(1 == nType) {
                    // 1|2017-08-31
                    prizeVO.setCouponExpiredDate(arr[1].replaceAll("-", ""));
                } else {
                    LogUtil.logError("invalid exprieDate. [{}]", prizeGoodsVO.getExpireDate(), couponInfoVO, reqData, prizeGoodsVO);
                }
            }

            prizeVO.setCouponNum(rspData.getCouponNum());
            nRet = couponMapper.updatePrizeStatus(prizeVO);
            if(nRet <= 0) {
                LogUtil.logError("updatePrizeStatus fail. [{}]", nRet, couponInfoVO, reqData, prizeVO);
                throw new AppException(AppErrorCode.ZLGOON_HTTP_ERROR);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            LogUtil.logException(e, couponVO, couponInfoVO, reqData);
            return rtnCodeMsg(AppErrorCode.ZLGOON_HTTP_ERROR);
        }

        return errCodeMsgVO;
    }

    // 쿠폰 상세조회
    public RspGetCouponDetailOld getCouponDetailOld(CouponInfoVO couponInfoVO, ReqGetCouponDetail reqData) throws Exception {
        RspGetCouponDetailOld rspData = null;

        try {
            // 즐거운 토큰 발급요청
            rspData = httpZlgoon.getCouponInfoOld(
                    reqData.getEventId(),
                    couponInfoVO.getCorpCode(),
                    reqData.getTrId());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            LogUtil.logException(e, couponInfoVO, reqData);
            throw new AppException(AppErrorCode.ZLGOON_HTTP_ERROR);
        }

        return rspData;
    }

    // 쿠폰 상세조회
    public RspGetCouponDetail getCouponDetail(CouponInfoVO couponInfoVO, ReqGetCouponDetail reqData) throws Exception {
        RspGetCouponDetail rspData = null;

        try {
            // 즐거운 토큰 발급요청
            rspData = httpZlgoon.getCouponDetail(
                    reqData.getEventId(),
                    couponInfoVO.getCorpCode(),
                    reqData.getTrId());

            // 즐거운에서 에러를 리턴하면 즐거운 에러메시지를 린턴한다.
            if(!"00".equalsIgnoreCase(rspData.getResultCode())) {
                throw new Exception(rspData.getResultMsg());
            }

            // 쿠폰정보 상세조회
            RspGetCouponDetailAdd detailAdd = couponMapper.getCouponDetailByTranId(reqData.getTrId());
            if(detailAdd == null) {
                throw new AppException(AppErrorCode.GET_PRIZE_INFO);
            }

            rspData.setDetailAdd(detailAdd);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            LogUtil.logException(e, couponInfoVO, reqData);
            throw new AppException(AppErrorCode.ZLGOON_SELLECT_ERROR);
        }

        // log.info("[{}] getCouponDetail.\n{}\n{}", VoUtil.toJson(reqData), VoUtil.toJson(rspData));
        return rspData;
    }

    // 당첨권 조회
    public PrizeVO getPrizeByGoodsIdNo(String goodsIdNo) throws Exception {
        return couponMapper.getPrizeByGoodsIdNo(goodsIdNo);
    }

    // 이벤트 조회(이벤트 seq)
    public EventVO getEventByEventSeq(String eventSeq) throws Exception {
        return couponMapper.getEventByEventSeq(eventSeq);
    }

    // 상품 조회(이벤트 seq)
    public List<GoodsListVO> getGoodsListByEventSeq(String goodsSeq) throws Exception {
        return couponMapper.getGoodsListByEventSeq(goodsSeq);
    }

    // 쿠폰함 이용가능 쿠폰리스트 조회
    public List<CouponListVO> getCouponBoxListAvail(KbReqCouponBoxVO reqData) throws Exception {
        return couponMapper.getCouponBoxListAvail(reqData);
    }

    // 쿠폰함 이력 조회
    public List<CouponListVO> getCouponBoxListHistory(CouponInfoVO reqData) throws Exception {
        return couponMapper.getCouponBoxListHistory(reqData);
    }
}
