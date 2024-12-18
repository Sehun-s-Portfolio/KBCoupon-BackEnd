package kbcp.site.kb.inquiry.service;

import kbcp.common.vo.ReqListVO;

import kbcp.site.kb.inquiry.constant.InquiryType;
import kbcp.site.kb.inquiry.mapper.InquiryMapper;
import kbcp.site.kb.inquiry.vo.InquiryDetailVO;
import kbcp.site.kb.inquiry.vo.InquiryInsertVO;
import kbcp.site.kb.inquiry.vo.InquiryListVO;
import kbcp.site.kb.inquiry.vo.InquiryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InquiryService {

    @Resource(name = "inquiryMapper")
    private InquiryMapper inquiryMapper;


    // 문의(상담) 등록
    @Transactional
    public int insertInquiry(String customerId, String inquiryTitle, String inquiryContent, String inquiryCode, String memberId) throws Exception {

        // view 에서 요청받은 DTO 객체 데이터를 기준으로 VO 객체로 변환 후 전달
        InquiryVO insertInquiryData = InquiryVO.builder()
                .inquiryTitle(inquiryTitle)
                .inquiryContent(inquiryContent)
                .createUser(customerId)
                .type(inquiryCode)
                .memberId(memberId)
                .build();

        return inquiryMapper.insertInquiry(insertInquiryData);
    }


    // 작성한 문의 리스트 조회
    public HashMap<String, Object> getInquiryList(String createUser, int month, Long page, String memberId) throws Exception {

        // 데이터 요청 객체 생성
        ReqListVO reqListVO = new ReqListVO();
        reqListVO.setPageStart((page - 1L) * 10L); // 페이지 시작 번호
        reqListVO.setPageLength(10L); // 페이지에 노출될 데이터 수
        reqListVO.setSearchKeyword(createUser); // 문의 작성자 id
        reqListVO.setSearchKeyword2(String.valueOf(month)); // 조회 개월 수
        reqListVO.setSearchKeyword3(memberId);

        // 작성한 문의 리스트 데이터 호출
        List<InquiryVO> inquiryList = inquiryMapper.getInquiryList(reqListVO);

        // 호출한 리스트 데이터를 반환 DTO 객체로 변환 후 생성
        List<InquiryListVO> inquiryResponseList = inquiryList.stream()
                .map(eachInquiry ->
                        InquiryListVO.builder()
                                .inquirySeq(eachInquiry.getInquirySeq())
                                .inquiryTitle(eachInquiry.getInquiryTitle())
                                .type(InquiryType.getInquiryTypeCodeData(eachInquiry.getType()))
                                .createUser(eachInquiry.getCreateUser())
                                .answerStatus(eachInquiry.getInquiryAnswer() != null ? "Y" : "N")
                                .answerStatusDate(eachInquiry.getInquiryAnswer() != null ?
                                        eachInquiry.getUpdateTime().substring(0, 10).replace("-", ".") + " 답변" : eachInquiry.getCreateTime().substring(0, 10).replace("-", ".") + " 문의")
                                .build()
                )
                .collect(Collectors.toList());

        // 작성한 문의 갯수 추출
        Long inquiryCount = inquiryMapper.getInquiryCount(createUser, memberId);
//        Long inquiryCount = inquiryMapper.getInquiryCount(reqListVO);


        // 반환 HashMap 객체 생성 및 반환 데이터 저장
        HashMap<String, Object> inquiryResponseData = new HashMap<>();
        inquiryResponseData.put("inquiryList", inquiryResponseList);
        inquiryResponseData.put("inquiryCount", inquiryCount);

        return inquiryResponseData;
    }


    // 문의 내용 상세 조회
    public InquiryDetailVO getInquiryDetail(Long inquirySeq, String memberId) throws Exception {

        // 문의 상세 내용 조회
        InquiryVO inquiryDetail = inquiryMapper.getInquiryDetail(inquirySeq, memberId);

        // 반환 DTO 데이터로 변환 후 반환
        return InquiryDetailVO.builder()
                .inquiryContent(inquiryDetail.getInquiryContent())
                .inquiryAnswer(inquiryDetail.getInquiryAnswer())
                .createUser(inquiryDetail.getCreateUser())
                .answerStatus(inquiryDetail.getInquiryAnswer() != null ? "Y" : "N")
                .build();
    }
}
