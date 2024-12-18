package kbcp.site.kb.api.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KbRspCouponInfoListVO extends KbRspVO {
    protected String array_size;     // 조회된 목록 개수(3자리)
    protected List<CouponInfoListVO> array;	// 조회결과
}

