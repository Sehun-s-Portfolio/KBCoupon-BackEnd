package kbcp.zlgoon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrCodeMsgVO extends AbstractVO {
    protected String errCode; 	  // 에러코드
    protected String errMsg;  // 에러메시지

    public ErrCodeMsgVO(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}

