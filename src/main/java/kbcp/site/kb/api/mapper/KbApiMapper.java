package kbcp.site.kb.api.mapper;

import kbcp.site.kb.api.vo.CouponInfoListReqVO;
import kbcp.site.kb.api.vo.CouponInfoListVO;
import kbcp.site.kb.api.vo.KbReqCouponInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KbApiMapper {
    // 쿠폰상태 조회
    List<CouponInfoListVO> getCouponInfoList(KbReqCouponInfoVO reqData) throws Exception;
}
