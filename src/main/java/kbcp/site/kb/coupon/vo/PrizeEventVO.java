package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import kbcp.svc.vo.EventVO;
import lombok.*;

@Getter
@Setter
@ToString
public class PrizeEventVO extends AbstractVO {
    private String eventSeq; 	// 이벤트 seq
    private String eventId; 	// 이벤트 id
    private String eventName;   // 프로모션명
    private String eventDayStart; 	// 이벤트명
    private String eventDayEnd; // 이벤트 시작일자
    private String expiredDate; // 이벤트 종료일자
    private String topText;     // 상단문구
    private String fileImgPath;	// 이미지 파일(fileImgPath)
    private String caution;	    // 유의사항
    private String btnNm;      // 버튼이름
    private String btnColor;   // 버튼색

    /////
    // 컨스트럭터가 아닌 별도 입출력 데이터.
    private String prizeSeq;	// 당첨권 Seq

    public PrizeEventVO(EventVO eventVO) {
        this.eventSeq = eventVO.getEventSeq();
        this.eventId = eventVO.getEventId();
        this.eventName = eventVO.getEventName();
        this.expiredDate = eventVO.getExpiredDate();
        this.topText = eventVO.getTopText();
        this.fileImgPath = eventVO.getFileImgPath();
        this.caution = eventVO.getCaution();
        this.btnNm = eventVO.getBtnNm();
        this.btnColor = eventVO.getBtnColor();
    }
}

