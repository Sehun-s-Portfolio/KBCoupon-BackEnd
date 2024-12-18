package kbcp.site.kb.inquiry.vo;
import kbcp.common.base.AbstractVO;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryInsertVO extends AbstractVO {
    private String resultText; // 고객사 및 회원 암호화 정보
    private String inquiryTitle; // 문의 타이틀
    private String inquiryContent; // 문의 내용
    private String type; // 문의 유형
    private String memberId; // 고객사 ID
}
