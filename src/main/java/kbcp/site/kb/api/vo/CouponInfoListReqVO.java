package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 쿠폰 발급 이력
@Getter
@Setter
@ToString
public class CouponInfoListReqVO extends AbstractVO {
    private String tranId;    // 고객사 tr
    private String clientId;    // 고객사 식별id('kbstart')
    private String customerId;  // 고객id
    private String startDay;    // 조회 시작일(포함)
    private String endDay;      // 조회 종료일(앞날짜까지 조회)
}
