package kbcp.zlgoon;

import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.network.Http;
import kbcp.common.util.LogUtil;

import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.site.kb.coupon.vo.RspGetCouponDetail;
import kbcp.site.kb.coupon.vo.RspGetCouponDetailOld;
import kbcp.zlgoon.vo.RspCouponHttpVO;
import kbcp.zlgoon.vo.RspOrderCouponHttpVO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HttpZlgoon extends Http {

    @Value("${site.network.zlgoon}")
    private String urlZlgoon;   // 즐거운 서버 URL

    private final int   EVENTID_LEN = 10;   // 프로모션ID(10)
    private final int   MEMBERID_LEN = 20;  // 고객사 식별ID(20)
    private final int   TRID_LEN = 50;      // 고객사 트랜잭션ID(50)

    // 쿠폰 발행
    // eventId: 프로모션ID(10)
    // goodsId: 판매상품ID(20)
    // rcvMdn: 수신자 MDN(20): "-" 없이 번호만. 예:0101234567
    // memberId: 고객사 식별ID(20)
    // trId: 고객사 트랜잭션ID(50)
    // tMsg: 전송제목(40)
    // cMsg: 전송내용(800)
    public RspOrderCouponHttpVO orderCoupon(final String eventId, final String goodsId,
                                            final String memberId, final String trId,
                                            final String tMsg, final String cMsg) throws Exception {
        String url = urlZlgoon + "/lssend/cslssend.hc"; // 쿠폰발행 URL
        Map<String, String> params = new HashMap<>();

        /////
        // 입력값 체크

        // eventId: 프로모션ID(10)
        if(StringUtil.isNull(eventId)) {
            LogUtil.logWarning("[{}] eventId empty error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 필수 입력값입니다.");
        }
        if(eventId.length() > EVENTID_LEN) {
            LogUtil.logWarning("[{}] eventId size error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 최대 " + EVENTID_LEN + "자 이내입니다.");
        }

        // goodsId: 판매상품ID(20)
        if(StringUtil.isNull(goodsId)) {
            LogUtil.logWarning("[{}] goodsId empty error. [{}]",
                    url, goodsId);
            throw new Exception("판매상품ID는 필수 입력값입니다.");
        }
        if(goodsId.length() > 20) {
            LogUtil.logWarning("[{}] goodsId size error. [{}]",
                    url, goodsId);
            throw new Exception("판매상품ID는 최대 20자 이내입니다.");
        }

        // memberId: 고객사 식별ID(20)
        if(StringUtil.isNull(memberId)) {
            LogUtil.logWarning("[{}] memberId empty error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 필수 입력값입니다.");
        }
        if(memberId.length() > MEMBERID_LEN) {
            LogUtil.logWarning("[{}] memberId size error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 최대 " + MEMBERID_LEN + "자 이내입니다.");
        }

        // trId: 고객사 트랜잭션ID(50)
        if(StringUtil.isNull(trId)) {
            LogUtil.logWarning("[{}] trId empty error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 필수 입력값입니다.");
        }
        if(trId.length() > TRID_LEN) {
            LogUtil.logWarning("[{}] trId size error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 최대 " + TRID_LEN + "자 이내입니다.");
        }

        /////
        // 필수입력
        params.put("EVENT_ID", eventId);
        params.put("GOODS_ID", goodsId);
        params.put("ORDER_CNT", "1");   // 고정
        params.put("RECEIVERMOBILE", "01000000000");   // 사용안하지만 에러가 나므로 고정값 세팅함.
        params.put("MEMBER_ID", memberId);
        params.put("TR_ID", trId);

        /////
        // 선택입력
        // tMsg: 전송제목(40)
        if(!StringUtil.isNull(tMsg)) {
            if(tMsg.length() > 40) {
                LogUtil.logWarning("[{}] tMsg size error. [{}]",
                        url, tMsg);
                throw new Exception("전송제목은 최대 40자 이내입니다.");
            }

            params.put("TMESSAGE", tMsg);
        }

        // cMsg: 전송내용(800)
        if(!StringUtil.isNull(cMsg)) {
            if(cMsg.length() > 800) {
                LogUtil.logWarning("[{}] cMsg size error. [{}]",
                        url, cMsg);
                throw new Exception("전송내용은 최대 800자 이내입니다.");
            }

            params.put("CMESSAGE", cMsg);
        }

        /////
        // http 통신
        String strRsp = request(url, params);
        if(StringUtil.isNull(strRsp)) {
            LogUtil.logFatal("[{}] empty return param. {}",
                    url, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터 공백 오류.");
        }

        Map<String, Object> mapData = new HashMap<>();
        String[] listRsp = strRsp.split(":");
        RspOrderCouponHttpVO rspData = new RspOrderCouponHttpVO();
        // 에러일때는 2개다.
        if(listRsp.length == 2 ) {
            rspData.setRspCode(listRsp[0]); // 에러코드
            rspData.setRspMsg(listRsp[1]);
            return rspData;
        }
        // 성공일때는 5개다.
        else if(listRsp.length == 5) {
            // [SUCCESS CODE]1
            rspData.setRspCode(listRsp[0]); // 성공코드
            rspData.setMdnNum(listRsp[1]);  // MDN 번호
            rspData.setMemberId(listRsp[2]); // 고객사 식별ID
            rspData.setTrId(listRsp[3]);     // TR ID
            rspData.setCouponNum(listRsp[4]);// 쿠폰번호
        } else if(listRsp.length == 6) {
                // [SUCCESS CODE]0
                rspData.setRspCode(listRsp[0]); // 성공코드
                rspData.setMdnNum(listRsp[1]);  // MDN 번호
                rspData.setOrderNum(listRsp[2]); // 주문번호
                rspData.setMemberId(listRsp[3]); // 고객사 식별ID
                rspData.setTrId(listRsp[4]);     // TR ID
                rspData.setCouponNum(listRsp[5]);// 쿠폰번호
        } else {
            LogUtil.logFatal("[{}] invalid return param cnt({}). [{}] {}",
                    url, listRsp.length, strRsp, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터수 오류.");
        }

        /////
        // 성공데이터 처리
        return rspData;
    }

    // 쿠폰 재전송
    // eventId: 프로모션ID(10)
    // memberId: 고객사 식별ID(20)
    // trId: 고객사 트랜잭션ID(50)
    public RspCouponHttpVO orderCouponRetry(final String eventId, final String memberId, final String trId) throws Exception {
        String url = urlZlgoon + "/lssend/resend.hc"; // 쿠폰 재전송 URL
        Map<String, String> params = new HashMap<>();

        /////
        // 입력값 체크

        // eventId: 프로모션ID(10)
        if(StringUtil.isNull(eventId)) {
            LogUtil.logWarning("[{}] eventId empty error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 필수 입력값입니다.");
        }
        if(eventId.length() > EVENTID_LEN) {
            LogUtil.logWarning("[{}] eventId size error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 최대 " + EVENTID_LEN + "자 이내입니다.");
        }

        // memberId: 고객사 식별ID(20)
        if(StringUtil.isNull(memberId)) {
            LogUtil.logWarning("[{}] memberId empty error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 필수 입력값입니다.");
        }
        if(memberId.length() > MEMBERID_LEN) {
            LogUtil.logWarning("[{}] memberId size error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 최대 " + MEMBERID_LEN + "자 이내입니다.");
        }

        // trId: 고객사 트랜잭션ID(50)
        if(StringUtil.isNull(trId)) {
            LogUtil.logWarning("[{}] trId empty error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 필수 입력값입니다.");
        }
        if(trId.length() > TRID_LEN) {
            LogUtil.logWarning("[{}] trId size error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 최대 " + TRID_LEN + "자 이내입니다.");
        }

        /////
        // 필수입력
        params.put("EVENT_ID", eventId);
        params.put("MEMBER_ID", memberId);
        params.put("TR_ID", trId);

        /////
        // http 통신
        String strRsp = request(url, params);
        if(StringUtil.isNull(strRsp)) {
            LogUtil.logFatal("[{}] empty return param. {}",
                    url, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터 공백 오류.");
        }

        Map<String, Object> mapData = new HashMap<>();
        String[] listRsp = strRsp.split(":");
        // 응답 필드는 3개다.
        if(listRsp.length != 3) {
            LogUtil.logFatal("[{}] invalid return param cnt({}). [{}] {}",
                    url, listRsp.length, strRsp, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터수 오류.");
        }

        /////
        // 결과값 분석
        RspCouponHttpVO rspData = new RspCouponHttpVO();
        rspData.setRspCode(listRsp[0]); // 성공코드
        rspData.setTrId(listRsp[1]);     // TR ID
        rspData.setMemberId(listRsp[2]); // 고객사 식별ID
        return rspData;
    }

    // 쿠폰 취소
    // eventId: 프로모션ID(10)
    // memberId: 고객사 식별ID(20)
    // trId: 고객사 트랜잭션ID(50)
    public RspCouponHttpVO cancelCoupon(final String eventId, final String memberId, final String trId) throws Exception {
        String url = urlZlgoon + "/lssend/allordercancel.hc"; // 쿠폰취소 URL
        Map<String, String> params = new HashMap<>();

        /////
        // 입력값 체크

        // eventId: 프로모션ID(10)
        if(StringUtil.isNull(eventId)) {
            LogUtil.logWarning("[{}] eventId empty error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 필수 입력값입니다.");
        }
        if(eventId.length() > EVENTID_LEN) {
            LogUtil.logWarning("[{}] eventId size error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 최대 " + EVENTID_LEN + "자 이내입니다.");
        }

        // memberId: 고객사 식별ID(20)
        if(StringUtil.isNull(memberId)) {
            LogUtil.logWarning("[{}] memberId empty error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 필수 입력값입니다.");
        }
        if(memberId.length() > MEMBERID_LEN) {
            LogUtil.logWarning("[{}] memberId size error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 최대 " + MEMBERID_LEN + "자 이내입니다.");
        }

        // trId: 고객사 트랜잭션ID(50)
        if(StringUtil.isNull(trId)) {
            LogUtil.logWarning("[{}] trId empty error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 필수 입력값입니다.");
        }
        if(trId.length() > TRID_LEN) {
            LogUtil.logWarning("[{}] trId size error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 최대 " + TRID_LEN + "자 이내입니다.");
        }

        /////
        // 필수입력
        params.put("EVENT_ID", eventId);
        params.put("MEMBER_ID", memberId);
        params.put("TR_ID", trId);

        /////
        // http 통신
        String strRsp = request(url, params);
        if(StringUtil.isNull(strRsp)) {
            LogUtil.logFatal("[{}] empty return param. {}",
                    url, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터 공백 오류.");
        }

        Map<String, Object> mapData = new HashMap<>();
        String[] listRsp = strRsp.split(":");
        // 응답 필드는 3개다.
        if(listRsp.length != 3) {
            LogUtil.logFatal("[{}] invalid return param cnt({}). [{}] {}",
                    url, listRsp.length, strRsp, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터수 오류.");
        }

        /////
        // 결과값 분석
        RspCouponHttpVO rspData = new RspCouponHttpVO();
        rspData.setRspCode(listRsp[0]); // 성공코드
        rspData.setTrId(listRsp[1]);     // TR ID
        rspData.setMemberId(listRsp[2]); // 고객사 식별ID
        return rspData;
    }

    // 쿠폰 상세조회
    // eventId: 프로모션ID(10)
    // memberId: 고객사 식별ID(20)
    // trId: 고객사 트랜잭션ID(50)
    public RspGetCouponDetailOld getCouponInfoOld(final String eventId, final String memberId, final String trId) throws Exception {
        String url = urlZlgoon + "/lssend/couponDetail.hc"; // 쿠폰 상세정보 URL
        Map<String, String> params = new HashMap<>();

        /////
        // 입력값 체크

        // eventId: 프로모션ID(10)
        if(StringUtil.isNull(eventId)) {
            LogUtil.logWarning("[{}] eventId empty error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 필수 입력값입니다.");
        }
        if(eventId.length() > EVENTID_LEN) {
            LogUtil.logWarning("[{}] eventId size error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 최대 " + EVENTID_LEN + "자 이내입니다.");
        }

        // memberId: 고객사 식별ID(20)
        if(StringUtil.isNull(memberId)) {
            LogUtil.logWarning("[{}] memberId empty error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 필수 입력값입니다.");
        }
        if(memberId.length() > MEMBERID_LEN) {
            LogUtil.logWarning("[{}] memberId size error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 최대 " + MEMBERID_LEN + "자 이내입니다.");
        }

        // trId: 고객사 트랜잭션ID(50)
        if(StringUtil.isNull(trId)) {
            LogUtil.logWarning("[{}] trId empty error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 필수 입력값입니다.");
        }
        if(trId.length() > TRID_LEN) {
            LogUtil.logWarning("[{}] trId size error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 최대 " + TRID_LEN + "자 이내입니다.");
        }

        /////
        // 필수입력
        params.put("EVENT_ID", eventId);
        params.put("MEMBER_ID", memberId);
        params.put("TR_ID", trId);

        /////
        // http 통신
        String strRsp = request(url, params);
        if(StringUtil.isNull(strRsp)) {
            LogUtil.logFatal("[{}] empty return param. {}",
                    url, VoUtil.StrMapToJson(params));
            throw new Exception("쿠폰발행 리턴 파라미터 공백 오류.");
        }

        String strRspJson = VoUtil.xmlToJson(strRsp);
        return VoUtil.fromJson(strRspJson, RspGetCouponDetailOld.class);
    }

    // 쿠폰 상세조회
    // eventId: 프로모션ID(10)
    // memberId: 고객사 식별ID(20)
    // couponNum: 쿠폰번호(50)
    public RspGetCouponDetail getCouponDetail(final String eventId, final String memberId, final String trId) throws Exception {
        String url = urlZlgoon + "/couponinquiry/notitalkInfo.do"; // 쿠폰 상세정보 URL
        Map<String, String> params = new HashMap<>();

        /////
        // 입력값 체크

        // eventId: 프로모션ID(10)
        if(StringUtil.isNull(eventId)) {
            LogUtil.logWarning("[{}] eventId empty error. [{}]",
                    url, eventId);
            throw new Exception("프로모션ID는 필수 입력값입니다.");
        }

        // memberId: 고객사 식별ID(20)
        if(StringUtil.isNull(memberId)) {
            LogUtil.logWarning("[{}] memberId empty error. [{}]",
                    url, memberId);
            throw new Exception("고객사 식별ID는 필수 입력값입니다.");
        }

        // couponNum: 쿠폰번호
        if(StringUtil.isNull(trId)) {
            LogUtil.logWarning("[{}] couponNum empty error. [{}]",
                    url, trId);
            throw new Exception("고객사 트랜잭션ID는 필수 입력값입니다.");
        }

        /////
        // 입력
        params.put("p", trId);
        params.put("t", "t"); // t=tr_id , c=couponNum 그외 org_id
        params.put("m", memberId);
        params.put("e", eventId);

        /////
        // http 통신
        String strRsp = request(url, params);
        if(StringUtil.isNull(strRsp)) {
            LogUtil.logFatal("[{}] empty return param. {}",
                    url, VoUtil.StrMapToJson(params));
            throw new AppException(AppErrorCode.JGN_SELECT_COUPON_DETAIL);
        }

        JSONArray jsonArray = new JSONArray(strRsp);
        if(jsonArray == null || jsonArray.length() == 0) {
            LogUtil.logFatal("[{}] invalid return value.\n[{}]\n[{}]",
                    url, VoUtil.StrMapToJson(params), strRsp);
            throw new AppException(AppErrorCode.JGN_SELECT_COUPON_DETAIL);
        }

        // 맨 위에값 하나를 빼온다.
        JSONObject json = jsonArray.getJSONObject(0);
        RspGetCouponDetail rspData = VoUtil.fromJson(json.toString(), RspGetCouponDetail.class);
        return rspData;
    }

}