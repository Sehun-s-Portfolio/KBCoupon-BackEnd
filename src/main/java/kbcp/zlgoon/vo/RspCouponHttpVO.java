package kbcp.zlgoon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RspCouponHttpVO extends AbstractVO {
    protected String rspCode; 	// 성공코드
    protected String memberId;  // 고객사 식별ID
    protected String trId;	    // TR ID
}
