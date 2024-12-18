package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CouponDetailMoveVO extends AbstractVO {
    protected String result_text; 	// 암호화된 데이터
    protected String goods_id; 	    // 상품코드(즐거운 EVENT_ID)
    protected String goods_id_no; 	// 고유 상품 id 일련번호
    protected String prev_page; 	// 이전 페이지
    protected String tran_id; 	// 트랜잭션 id
    protected String event_id; 	// 이벤트 id
}
