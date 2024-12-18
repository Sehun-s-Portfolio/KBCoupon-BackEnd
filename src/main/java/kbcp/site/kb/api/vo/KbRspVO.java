package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KbRspVO extends AbstractVO {
    protected String tran_id; 	    // tr
    protected String result_code; 	// 응답 코드
    protected String result_msg; 	// 응답 메시지
}

