package kbcp.site.kb.inquiry.vo;

import kbcp.common.base.AbstractVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InquiryDetailVO extends AbstractVO {
    private String inquiryContent; // 문의 내용
    private String inquiryAnswer; // 문의 답변 내용
    private String createUser; // 문의자 멤버쉽 코드
    private String answerStatus; // 답변 상태
}
