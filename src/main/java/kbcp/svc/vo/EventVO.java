package kbcp.svc.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventVO extends AbstractVO {
    private String eventSeq;    // 이벤트 seq
    private String eventId;     // 이벤트 id(즐거운 생성)
    private String memberId;    // 고객사 식별id
    private String eventName;   // 프로모션명
    private String expiredDate; // 당첨권 선택가능 기간
    private String topText;     // 상단문구
    private String fileImgNm;   // 원본파일명
    private String fileImgPath; // 파일상대경로
    private String fileImgKey;  // uuid 파일키
    private String caution;     // 유의사항
    private String btnNm;     // 유의사항
    private String btnColor;     // 유의사항
    private String createTime;  // 생성일자
    private String createUser;  // 생성자
    private String updateTime;  // 수정일자
    private String updateUser;  // 수정자
}
