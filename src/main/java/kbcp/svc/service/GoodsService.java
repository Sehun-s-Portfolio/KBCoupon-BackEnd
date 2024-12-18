package kbcp.svc.service;

import kbcp.svc.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodsService {

    @Resource(name = "goodsMapper")
    private GoodsMapper goodsMapper;


}
