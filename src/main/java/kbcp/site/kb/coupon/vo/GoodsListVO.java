package kbcp.site.kb.coupon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoodsListVO extends AbstractVO {
    private String eventSeq;
    private String goodsSeq;
    private String goodsId;
    private String brandName;
    private String goodsName;
    private String fileImgPath;
    private String fileImgKey;
    private String defaultYn;
    private String goodsIdNo;
    private String goodsColor;
}
