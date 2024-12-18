package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KbReqSelectPrizeVO extends AbstractVO {
    private String tran_id; 	// tr
    private String customer_id; // 회원번호
    private String corp_code; 	// 고객사 식별id
    private String goods_id; 	// 상품ID: 5(난수)+10(즐거운 상품ID)
    private String goods_id_no; // 상품코드 순번(상품 고유값)
}

