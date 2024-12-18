package kbcp.svc.vo;

import kbcp.common.base.AbstractVO;
import lombok.*;

@Getter
@Setter
@ToString
public class PrizeVO extends AbstractVO {
    private String prizeSeq; 	// 당첨권 seq
    private String eventSeq; 	// 이벤트 seq
    private String eventId; 	// 이벤트 id
    private String goodsSeq; 	// 상품 seq
    private String goodsId; 	// 상품 seq
    private String goodsSeqBefore; 	// 이전 상품 seq
    private String tranId;      // 고객사 tr
    private String corpCode;    // 고객사 식별id(즐거운 memeber_id)
    private String customerId;  // 고객id
    private String goodsIdNo;   // 상품코드 순번
    private String reqCode;     // 10:발급 요청, 20:재발급 요청
    private String runBatch;    // 쿠폰 배치발급여부(0:일반, 1:배치발급)
    private String couponNum;   // 쿠폰번호(즐거운 생성)
    private String couponTime;  // 쿠폰발급 요청/응답시간
    private String couponUseTime;       // 쿠폰 사용시간
    private String couponExpiredDate;   // 쿠폰 만료일
    private String couponStatus; // 01:사용, 02:미사용
    private String status;      // 쿠폰상태(0:당첨권 접수, 1:쿠폰발급완료, 2:교환완료, 3:교환취소, 4:알수없음, 9:삭제)
    private String createTime;  // 생성일자
    private String updateTime;  // 수정일자
    private String updateUser;  // 수정자
}
