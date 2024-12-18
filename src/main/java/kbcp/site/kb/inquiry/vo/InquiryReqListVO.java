package kbcp.site.kb.inquiry.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InquiryReqListVO extends AbstractVO {
    private String resultText;
    private String month;
    private String page;
    private String memberId;
}
