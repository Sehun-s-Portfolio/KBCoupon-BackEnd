package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqSelectCoupon extends AbstractVO {
    private String couponInfo; 	// couponInfo가 암호화된 result_text
    private String goodsIdNo; 	// 상품일련번호
    private String prevPage; // 접속 쿠폰함 페이지
}
