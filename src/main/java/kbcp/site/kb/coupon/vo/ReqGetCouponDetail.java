package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqGetCouponDetail extends AbstractVO {
    protected String couponInfo; 	// 암호화된 couponInfo
    protected String trId; 	        // tr
    protected String eventId; 	    // 이벤트 ID (goods_id)
    protected String prevPage; 	    // 이전 페이지
    protected String prizeSeq;
 }
