package kbcp.svc.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 쿠폰 발급 이력
@Getter
@Setter
@ToString
public class CouponVO extends AbstractVO {
    private String couponSeq;  // 쿠폰 seq
    private String cmd;        // 0:쿠폰발행, 1:쿠폰재전송, 3:쿠폰취소
    private String prizeSeq;   // 당첨권 seq
    private String eventId;    // 이벤트 id
    private String trId;        // 고객사 tr
    private String memberId;   // 고객사 식별id
    private String goodsId;    // 상품 id
    private String runBatch;    // 쿠폰 배치발급여부(0:일반, 1:배치발급)
    private String rspCode;    // 응답코드
    private String rspMsg;     // 에러메시지
    private String couponNum;  // 쿠폰번호(즐거운 생성)
    private String orderNum;   // 주문번호
    private String reqTime;   // 요청일자
    private String rspTime;   // 응답일자
}
