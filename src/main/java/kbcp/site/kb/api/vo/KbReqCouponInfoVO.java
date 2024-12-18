package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KbReqCouponInfoVO extends AbstractVO {
    private String tran_id; 	    // tr
    private String customer_id;     // 회원번호
    private String corp_code; 	    // 고객사 식별id
    protected String start_day; 	// 조회시작일
    protected String end_day; 	    // 조회종료일
}
