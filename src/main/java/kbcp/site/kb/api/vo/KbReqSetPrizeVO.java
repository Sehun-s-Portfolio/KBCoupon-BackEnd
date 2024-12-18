package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KbReqSetPrizeVO extends AbstractVO {
    private String tran_id; 	// tr
    private String customer_id; // 회원번호
    private String corp_code; 	// 고객사 식별id
    private String req_code; 	// 10:발급 요청, 20:재발급 요청
    private String goods_id; 	// 상품ID: 고객사 이벤트 코드(5)+즐거운 상품ID(10)
    private String goods_id_no; // 상품코드 순번(상품 고유값)

}

