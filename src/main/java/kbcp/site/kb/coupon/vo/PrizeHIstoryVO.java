package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PrizeHIstoryVO extends AbstractVO {
    private String prizeSeq; // prize seq
    private String goodsSeq; // goods seq
    private String goodsName; // 제품 명
    private String goodsPrice; // 제품 가격
    private String createTime; // 변경 이력 생성 일자
}
