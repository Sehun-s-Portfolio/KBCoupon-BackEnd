package kbcp.site.kb.coupon.mapper;

import kbcp.site.kb.api.vo.KbReqCouponBoxVO;
import kbcp.site.kb.coupon.vo.*;
import kbcp.svc.vo.CouponVO;
import kbcp.svc.vo.EventVO;
import kbcp.svc.vo.GoodsVO;
import kbcp.svc.vo.PrizeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface KbCouponMapper {

    // 당첨권 조회(상품코드순번)
    PrizeVO getPrizeByGoodsIdNo(String goodsIdNo) throws Exception;
    // 쿠폰정보 조회(쿠폰발급시)
    PrizeGoodsVO getPrizeByCouponInfo(CouponInfoVO reqData) throws Exception;

    // 당첨권 등록
    int insertPrize(PrizeVO reqData) throws Exception;
    // 쿠폰상태 변경
    int updatePrizeStatus(PrizeVO reqData) throws Exception;
    // 쿠폰 발급 이력 등록
    int insertCoupon(CouponVO reqData) throws Exception;
    // 쿠폰 발급 응답 update
    int updateCoupon(CouponVO reqData) throws Exception;

    // 쿠폰함 이용가능 쿠폰리스트 조회
    List<CouponListVO> getCouponBoxListAvail(KbReqCouponBoxVO reqData) throws Exception;
    // 쿠폰함 이력 조회
    List<CouponListVO> getCouponBoxListHistory(CouponInfoVO reqData) throws Exception;

    // 상품/이벤트 정보 조회(상품ID)
    GoodsEventVO getGoodsEventByGoodsId(Map reqData) throws Exception;
    // 상품 조회(이벤트 seq)
    List<GoodsListVO> getGoodsListByEventSeq(String goodsSeq) throws Exception;
    // 상품 조회(상품 seq)
    GoodsVO getGoodsByGoodsSeq(String goodsSeq) throws Exception;
    // 이벤트 조회(이벤트 seq)
    EventVO getEventByEventSeq(String eventSeq) throws Exception;
    // 쿠폰정보 상세조회(쿠폰상세조회시)
    RspGetCouponDetailAdd getCouponDetailByTranId(String tranId) throws Exception;
}