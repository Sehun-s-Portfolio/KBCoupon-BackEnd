package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RspCouponDetail extends AbstractVO {
        private String member_id; 	//
        private String event_id; 	//
        private String tr_id; 	    //
        private RspOrgId org_id;    //
 }
