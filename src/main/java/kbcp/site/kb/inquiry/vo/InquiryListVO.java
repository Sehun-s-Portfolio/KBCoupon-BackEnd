package kbcp.site.kb.inquiry.vo;

import kbcp.common.base.AbstractVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InquiryListVO extends AbstractVO {
    private String inquirySeq; // 문의 id
    private String inquiryTitle; // 문의 타이틀
    private String type; // 문의 유형
    private String createUser; // 문의자 멤버십 코드
    private String answerStatus; // 답변 상태
    private String answerStatusDate; // 답변 혹은 문의 일자
}
