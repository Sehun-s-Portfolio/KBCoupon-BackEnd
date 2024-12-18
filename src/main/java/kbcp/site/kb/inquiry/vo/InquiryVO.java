package kbcp.site.kb.inquiry.vo;

import kbcp.common.base.AbstractVO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class InquiryVO extends AbstractVO {
    private String inquirySeq; // 문의 seq
    private String inquiryTitle; // 문의 타이틀
    private String inquiryContent; // 문의 내용
    private String inquiryAnswer; // 문의 답변
    private String createUser; // 문의자 계정
    private String updateUser; // 문의자 계정
    private String type; // 문의 유형
    private String memberId; // 고객사 ID
    private String createTime; // 문의 시간
    private String updateTime; // 답변 시간
}
