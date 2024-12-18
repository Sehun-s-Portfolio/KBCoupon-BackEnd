package kbcp.zlgoon.vo;

import lombok.*;

@Getter
@Setter
@ToString
public class RspOrderCouponHttpVO extends RspCouponHttpVO {
    private String rspCode; 	// 코드
    private String rspMsg; 	    // 메시지
    private String mdnNum; 	    // MDN 번호
    private String orderNum;    // 주문번호
    private String couponNum;	// 쿠폰번호
}
