package kbcp.scheduler.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueCouponListVO extends AbstractVO {
    private String prizeSeq;    // 당첨권 seq
    private String corpCode;    // 고객사 식별id(즐거운 memeber_id)
    private String eventId;     // 이벤트 ID
    private String goodsSeq;    // 상품 seq
    private String goodsId;     // 상품ID(즐거운발급)
    private String tranId;      // tr
    private String expiredDate; // 당첨권 시한(쿠폰 강제발급)
    private String defaultYn;   // 당첨권 쿠폰 기본 설정 값
    private String createTime;  // 당첨권 발급일시(YYYYMMDD)
}
