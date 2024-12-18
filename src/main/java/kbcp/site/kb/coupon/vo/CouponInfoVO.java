package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CouponInfoVO extends AbstractVO {
    protected String prizeSeq;
    protected String corpCode; 	    // 고객사 식별ID
    protected String eventId; 	    // 이벤트ID
    protected String customerId; 	// 고객 고유번호
}
