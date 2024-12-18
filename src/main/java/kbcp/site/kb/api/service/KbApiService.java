package kbcp.site.kb.api.service;

import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.site.kb.api.crypto.KbCryptoSeed;
import kbcp.site.kb.api.mapper.KbApiMapper;
import kbcp.site.kb.api.vo.*;
import kbcp.site.kb.coupon.vo.GoodsEventVO;
import kbcp.svc.AppCryptoRsa;
import kbcp.site.kb.coupon.mapper.KbCouponMapper;
import kbcp.svc.vo.PrizeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class KbApiService {
    private final KbCryptoSeed kbCryptoSeed;   // KB RSA 모듈

    @Resource(name = "kbApiMapper")
    private KbApiMapper kbApiMapper;

    @Resource(name = "kbCouponMapper")
    private KbCouponMapper kbCouponMapper;

    // KB 데이터 암호화
    public String encodeKbCrypto(String strEncode) {
        if (StringUtil.isNull(strEncode)) {
            LogUtil.logError("encode text empty.");
            return null;
        }

        String strText = null;

        try {
            /////
            // 복호화.
            strText = kbCryptoSeed.encode(strEncode);
            if (StringUtil.isNull(strText)) {
                LogUtil.logError("decoded text empty. [{}]", strEncode);
                return strText;
            }
        } catch (Exception e) {
            LogUtil.logException(e);
            return strText;
        }

        return strText;
    }

    // KB 데이터 복호화
    public String decodeKbCrypto(String strDecode) {
        if (StringUtil.isNull(strDecode)) {
            LogUtil.logError("decode text empty.");
            return null;
        }

        String strText = null;

        try {
            /////
            // 복호화.
            strText = kbCryptoSeed.decode(strDecode);
            if (StringUtil.isNull(strText)) {
                return strText;
            }
        } catch (Exception e) {
            return strText;
        }

        return strText;
    }

    // 당첨권 등록
    @Transactional
    public int insertPrize(PrizeVO reqData) throws Exception {
        // 상품ID로 상품/이벤트 정보를 조회한다.
        // 상품 ID: 고객사 이벤트 코드(5) + 상품ID(10)
        Map<String, String> selReq = new HashMap<>();
        selReq.put("corpEventCode", reqData.getGoodsId().substring(0, 5));
        selReq.put("goodsId", reqData.getGoodsId().substring(5));
        GoodsEventVO goodsEventVO = kbCouponMapper.getGoodsEventByGoodsId(selReq);
        if(goodsEventVO == null) {
            LogUtil.logError("not found goodsId. [{}][{}]",
                    selReq.get("corpEventCode"), selReq.get("goodsId"));
            throw new AppException(AppErrorCode.KB_NO_GOODS_ID);
        }

        // 조회된 상품이 기본상품이 아니면 그냥 로그만 남긴다.
        if(!goodsEventVO.getDefaultYn().equalsIgnoreCase("Y")) {
            LogUtil.logWarning("goods is not default.", reqData, goodsEventVO);
        }

        reqData.setEventSeq(goodsEventVO.getEventSeq());
        reqData.setGoodsSeq(goodsEventVO.getGoodsSeq());
        return kbCouponMapper.insertPrize(reqData);
    }

    // 쿠폰상태 조회
    public List<CouponInfoListVO> getCouponInfoList(KbReqCouponInfoVO reqData) throws Exception {
        return kbApiMapper.getCouponInfoList(reqData);
    }

}
