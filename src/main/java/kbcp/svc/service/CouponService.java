package kbcp.svc.service;

import kbcp.svc.mapper.CouponMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CouponService {

    @Resource(name = "couponMapper")
    private CouponMapper couponMapper;


}
