package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import kbcp.svc.vo.EventVO;
import lombok.*;

@Getter
@Setter
@ToString
public class GoodsEventVO extends AbstractVO {
    private String goodsSeq; 	// 상품 seq
    private String defaultYn; 	// 상품 디폴트 여부
    private String eventSeq;    // 이벤트 seq
    private String eventId; 	// 이벤트 ID
}
