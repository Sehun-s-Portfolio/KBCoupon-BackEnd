package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqGetCoupon extends AbstractVO {
    private String couponInfo; 	// 암호화된 couponInfo
    private String goodsSeq;    // 발급받을 상품 seq
    private String goodsId;     // 발급받을 상품 ID
    private String runBatch="0";// 쿠폰 배치발급여부(0:일반, 1:배치발급)
}
