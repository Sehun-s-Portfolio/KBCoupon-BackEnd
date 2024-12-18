package kbcp.site.kb.faq.vo;

import kbcp.common.base.AbstractVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FaqVO extends AbstractVO {
    private String faqSeq; // FAQ id
    private String faqTitle; // FAQ 타이틀
    private String faqContent; // FAQ 내용
    private String faqSt; // FAQ 노출 여부
    private String createTime; // FAQ 생성 일자
    private String updateTime; // FAQ 수정 일자
}
