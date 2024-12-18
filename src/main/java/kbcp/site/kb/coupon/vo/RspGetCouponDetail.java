package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RspGetCouponDetail extends RspGetCouponDetailAdd {
    private String resultCode; 	    // 성공[00], 실패[그외]
    private String resultMsg; 	    // 성공[SUCCESS], 실패[그 외]
    private String rcompanyNameDisp; 	// 브랜드명 - (*) 브랜드 명 1
    private String couponMsgTitle; 	    // 쿠폰발행 문자 발송 제목
    private String couponMsgUseInfo; 	// 쿠폰발행 문자 발송 이용안내 - (*) 유의사항
    private String templateImgType; 	// 판매상품 템플릿 타입
    private String templateImgPathCdn; 	// 판매상품 템플릿 CDN 저장 위치 - (*) 쿠폰 전체 이미지 관련 2
    private String templateImgHostCdn; 	// 판매상품 템플릿 CDN 저장 위치 HOST - (*) 쿠폰 전체 이미지 관련 1
    private String templateImgName; 	// 판매상품 템플릿 파일명 - (*) 쿠폰 전체 이미지 관련 3
    private String balanceURL; 	    // 잔용조회 웹페이지 링크 URL
    private String rcompanyId; 	    // 브랜드코드
    private String couponNumber; 	// 쿠폰번호 - (*) 바코드 변환
    private String productType; 	// 쿠폰 타입
    private String couponName; 	    // 쿠폰명 - (*) 쿠폰명
    private String goodsTotalPrice; // 쿠폰 액면가
    private String remainAmount; 	// 쿠폰 잔액
    private String exchangeEndDate; // 유효기간 만료일 - (*) 유효기간 만료일
    private String exchangeBrand; 	// 사용 브랜드명 - (*) 브랜드 명 관련 2
    private String exchangeBranch; 	// 사용 매장명 - (*) 교환처
    private String exchangeDate; 	// 교환일시 - (*) 교환 일시
    private String claimType; 	    // 발행취소 여부
    private String claimDate; 	    // 발행취소 일시
    private String exchangeStatus; 	// 사용상태 - (*) 사용(교환?) 가능 상태
    private String refund_status; 	// 환불상태
    private String isRefundProgress;// 고객접수 환불 진행 가능여부
    private String cbPhone; 	    // 회신번호

    public void setDetailAdd(RspGetCouponDetailAdd addData) {
        this.modDate = addData.modDate;         // 쿠폰 수정일
        this.runBatch = addData.runBatch;       // 쿠폰 배치발급여부(0:일반, 1:배치발급)
        this.status = addData.status;           // 쿠폰 상태
        this.brandName = addData.brandName;     // 최종 브랜드명
        this.goodsName = addData.goodsName;     // 최종 상품명
        this.beforeBrand = addData.beforeBrand; // 이전 브랜드명
        this.beforeGoods = addData.beforeGoods; // 이전 상품명
        this.bacodeYn = addData.bacodeYn;       // 바코드 생성유무(y:생성, n:미생성)
    }
 }
