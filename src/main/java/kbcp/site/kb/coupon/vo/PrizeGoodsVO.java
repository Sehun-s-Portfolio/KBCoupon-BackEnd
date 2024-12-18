package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.*;

@Getter
@Setter
@ToString
public class PrizeGoodsVO extends AbstractVO {
    private String prizeSeq; 	// prize seq
    private String tranId;      // tr
    private String status;      // 상태
    private String goodsSeq;    // goods seq
    private String goodsId;     // 상품 ID
    private String defaultYn;   // 디폴트 여부
    private String statusUpdateYn;   // Y:쿠폰사용여부 알림, N:쿠폰사용여부 모름
    private String expireDate;  //     `expire_date` varchar(15) default null comment '상품 유효기간 : 주문일로부터 (주문당일 1일 기준) 1 : 일까지 Ex ) 0|180 1|2017-08-31',
}
