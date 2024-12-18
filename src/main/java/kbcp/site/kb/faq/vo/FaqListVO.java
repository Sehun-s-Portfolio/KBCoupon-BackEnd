package kbcp.site.kb.faq.vo;

import kbcp.common.base.AbstractVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FaqListVO extends AbstractVO {
    private String faqSeq; // FAQ id
    private String faqTitle; // FAQ 타이틀
    private String faqSt; // FAQ 노출 여부
}
