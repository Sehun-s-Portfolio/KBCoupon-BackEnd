package kbcp.svc.service;

import kbcp.svc.mapper.EventMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EventService {

    @Resource(name = "eventMapper")
    private EventMapper eventMapper;


}
