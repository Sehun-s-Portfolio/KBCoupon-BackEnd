package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KbReqVO extends AbstractVO {
    protected String result_text; 	// 암호화된 데이터
}
