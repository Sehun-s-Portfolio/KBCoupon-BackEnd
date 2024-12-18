package kbcp.svc.service;

import kbcp.svc.mapper.PrizeMapper;
import kbcp.svc.vo.PrizeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class PrizeService {

    @Resource(name = "prizeMapper")
    private PrizeMapper prizeMapper;


}
