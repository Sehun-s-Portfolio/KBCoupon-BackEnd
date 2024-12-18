package kbcp.site.kb.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 쿠폰 발급 이력
@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponInfoListVO extends AbstractVO {
    private String goodsId;     // 이벤트ID
    private String goodsIdNo;   // 상품코드 순번
    private String status;      // 쿠폰상태(0:당첨권 접수, 1:쿠폰발급완료, 2:교환완료, 3:교환취소, 4:알수없음, 9:삭제)
    private String pd1Id;       // 최초 상품 id(즐거운 생성)
    private String pd1GetTime;  // 최초 경품 발급일시
    private String pd1Name;     // 최초 상품명
    private String pd1Price;    // 최초 가격
    private String pd2Id;       // 최종 상품 id(즐거운 생성)
    private String pd2GetTime;  // 최종 경품 선택일시
    private String pd2Name;     // 최종 상품명
    private String pd2Price;    // 최종 가격
    private String pdUseGubun;  // 쿠폰 사용여부(01:사용, 02:미사용)
    private String pdUseTime;   // 쿠폰사용일시
    private String pdCalPrice;  // 쿠폰사용일시
}
