package kbcp.svc.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoodsVO extends AbstractVO {
    private String goodsSeq;    // 상품 seq
    private String goodsId;     // 상품 id(즐거운 생성)
    private String eventSeq;    // 이벤트 seq
    private String goodsName;   // 상품명
    private String brandName;   // 브랜드명
    private String price;       // 가격
    private String defaultYn;   // 디폴트 상품 여부(y:디폴트, n:디폴트 아님)
    private String statusUpdateYn;   // Y:쿠폰사용여부 알림, N:쿠폰사용여부 모름
    private String expireDate;  //     `expire_date` varchar(15) default null comment '상품 유효기간 : 주문일로부터 (주문당일 1일 기준) 1 : 일까지 Ex ) 0|180 1|2017-08-31',
    private String fileImgNm;   // 원본파일명
    private String fileImgPath; // 파일상대경로
    private String fileImgKey;  // uuid 파일키
    private String createTime;  // 생성일자
    private String createUser;  // 생성자
    private String updateTime;  // 수정일자
    private String updateUser;  // 수정자
}
