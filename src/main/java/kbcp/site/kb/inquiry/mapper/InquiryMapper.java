package kbcp.site.kb.inquiry.mapper;

import kbcp.common.vo.ReqListVO;
import kbcp.site.kb.inquiry.vo.InquiryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InquiryMapper {

    // 문의(상담) 등록
    int insertInquiry(InquiryVO reqData) throws Exception;

    // 작성한 문의 리스트 조회
    List<InquiryVO> getInquiryList(ReqListVO reqListVO) throws Exception;

    // 문의 상세 내용 조회
    InquiryVO getInquiryDetail(Long inquirySeq, String memberId) throws Exception;

    // 작성 문의 갯수 조회
    Long getInquiryCount(String createUser, String memberId) throws Exception;
//    Long getInquiryCount(ReqListVO reqListVO) throws Exception;
}
