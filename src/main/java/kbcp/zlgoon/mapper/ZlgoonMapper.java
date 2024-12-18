package kbcp.zlgoon.mapper;

import kbcp.site.kb.coupon.vo.*;
import kbcp.svc.vo.PrizeVO;
import org.apache.ibatis.annotations.Mapper;
import kbcp.zlgoon.vo.ReqExchageVO;
import java.util.List;

@Mapper
public interface ZlgoonMapper {
    // 쿠폰 교환내역 등록
    int insertExchange(ReqExchageVO reqData) throws Exception;
    // 쿠폰 테이블에 교환내역 적용
    int updatePrizeExchange(PrizeVO reqData) throws Exception;

}