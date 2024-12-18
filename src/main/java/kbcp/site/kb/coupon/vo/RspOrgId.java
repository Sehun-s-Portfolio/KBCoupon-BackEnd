package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RspOrgId extends AbstractVO {
        private String value; 	    // orgId
        private String order_id; 	// 주문번호
        private String goods_name; 	// 상품명
        private String order_date; 	// 주문일
        private String order_cnt; 	// 주문수량(1)
        private String barcode_num; // 쿠폰번호
        private String sms_type; 	// 문자전송타입 (MMS, SMS, LMS)
        private String sms_date; 	// 문자전송일시
        private String sms_status; 	// 문자전송상태
        private String exchange_date; 	// 교환일시
        private String exchange_status; // 0:교환대기, 1:교환완료, 3:교환취소
        private String exchange_over; 	// Y : 교환기간만료, N : 교환가능
        private String claim_type; 	// 주문취소상태 N :미 취소, CC : 주문취소
        private String claim_date; 	// 주문취소일시
        private String rcompany_name; 	// 제휴사명
        private String branch_name; 	// 교환지점명
        private String end_date; 	    // 시작일 ~ 종료일 (자정까지)
        private String goods_price; 	// 상품가격
        private String order_balance; 	// 쿠폰 잔액
        private String first_exchange_date; // 최초로 교환한 시간을 표기
 }
