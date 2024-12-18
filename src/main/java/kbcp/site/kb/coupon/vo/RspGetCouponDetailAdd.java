package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RspGetCouponDetailAdd extends AbstractVO {
    protected String modDate;     // 쿠폰 수정일
    protected String runBatch;    // 쿠폰 배치발급여부(0:일반, 1:배치발급)
    protected String status;      // 쿠폰상태(0:당첨권 접수, 1:쿠폰발급완료, 2:교환완료, 3:교환취소, 4:알수없음, 9:삭제)
    protected String bacodeYn;    // 바코드 생성유무(y:생성, n:미생성)
    protected String brandName;   // 최종 브랜드명
    protected String goodsName;   // 최종 상품명
    protected String beforeBrand; // 이전 브랜드명
    protected String beforeGoods; // 이전 상품명
 }
