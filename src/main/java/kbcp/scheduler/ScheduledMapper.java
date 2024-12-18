package kbcp.scheduler;

import kbcp.scheduler.vo.IssueCouponListVO;
import kbcp.svc.vo.CouponVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduledMapper {
    // 강제 쿠폰 발급 대상 리스트 조회(쿠폰 미발급 2일 경과)
    List<IssueCouponListVO> getIssueCouponList() throws Exception;
    // 쿠폰 발급 에러시 쿠폰 상태 변경.
    int updateRunBatch(String prizeSeq) throws Exception;
}