package kbcp.site.kb.faq.mapper;

import kbcp.site.kb.faq.vo.FaqListVO;
import kbcp.site.kb.faq.vo.FaqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqMapper {

    // FAQ 상세 조회
    FaqVO getFaq(Long faqSeq) throws Exception;

    // FAQ 페이지 노출 리스트 조회
    List<FaqListVO> getFaqList(String memberId) throws Exception;
}