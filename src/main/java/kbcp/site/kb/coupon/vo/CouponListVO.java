package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CouponListVO extends AbstractVO {
    private String prizeSeq;    // 당첨권 seq
    private String tranId;      // tr
    private String goodsIdNo;   // 상품일련번호(KB발급)
    private String goodsId;     // 상품ID(즐거운발급)
    private String eventId;     // 이벤트 ID
    private String brandName;   // 브랜드 명
    private String goodsName;   // 굿즈 명
    private String fileImgPath; // 이미지 파일 경로
    private String fileImgKey;  // 이미지 파일 키
    private String defaultYn;   // 당첨권 쿠폰 기본 설정 값
    private String expiredYn;  // 쿠폰만료여부(Y:만료, N:사용가능)
    private String status;  // 상태
    private String runDate; // status = 0 (남은 만료 일 수)
    private String couponExpiredDate; // 쿠폰만료일
}
