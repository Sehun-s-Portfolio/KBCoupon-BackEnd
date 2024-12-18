package kbcp.zlgoon.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RspExchangeVO extends AbstractVO {
    private String ver; 	    // v10
    private String trId;	    // PUSH_ID 내역 전달 id
    private String dateTime;	// 전송일시
    private String errmessage;	// 에러메시지
    private String resCode;     // 성공 : 0000, 실패 : 9999
}

