package kbcp.site.kb.faq.service;

import kbcp.site.kb.faq.mapper.FaqMapper;
import kbcp.site.kb.faq.vo.FaqListVO;
import kbcp.site.kb.faq.vo.FaqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class FaqService {

    @Resource(name = "faqMapper")
    private FaqMapper faqMapper;

    // FAQ 상세 조회
    public FaqVO getFaq(Long faqSeq) throws Exception {
        return faqMapper.getFaq(faqSeq);
    }


    // FAQ 페이지 노출 리스트 조회
    public List<FaqListVO> getFaqList(String memberId) throws Exception {
        return faqMapper.getFaqList(memberId);
    }

}
