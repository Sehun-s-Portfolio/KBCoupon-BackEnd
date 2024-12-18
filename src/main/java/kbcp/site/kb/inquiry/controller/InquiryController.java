package kbcp.site.kb.inquiry.controller;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.common.vo.ResultObjVO;
import kbcp.common.vo.ResultVO;
import kbcp.site.kb.coupon.service.KbCouponService;
import kbcp.site.kb.coupon.vo.CouponInfoVO;
import kbcp.site.kb.inquiry.constant.InquiryType;
import kbcp.site.kb.inquiry.service.InquiryService;
import kbcp.site.kb.inquiry.vo.InquiryDetailVO;
import kbcp.site.kb.inquiry.vo.InquiryInsertVO;
import kbcp.site.kb.inquiry.vo.InquiryReqListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController extends AbstractController {

    private final InquiryService inquiryService;
    private final KbCouponService kbCouponService;

    // 복호화된 회원 및 고객사 정보 검증 예외 처리
    private String encodeDataProve(CouponInfoVO couponInfoVO){
        // 복호화된 데이터가 존재하지 않을 시 예외 처리
        if(couponInfoVO == null) {
            return "couponInfoVO is null.";
        }

        // 회원 멤버쉽 코드 확인
        if(StringUtil.isNull(couponInfoVO.getCustomerId())) {
            return "customer_id empty.";
        }

        // 고객사 식별 ID 확인
        if(StringUtil.isNull(couponInfoVO.getCorpCode())) {
            return "corp_code empty.";
        }

        return null;
    }


    // 문의(상담) 등록
    @PostMapping(value = "/insertInquiry.do")
    @ResponseBody
    public ResultVO insertInquiry(HttpServletRequest request, HttpServletResponse response, @RequestBody InquiryInsertVO reqData) {

        ResultVO result = new ResultVO();

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());
            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if(!StringUtil.isNull(strRet)){
                LogUtil.logError(strRet, request, reqData);
                result.setErrorMsg("회원 및 고객사 인증 정보 에러");
                return result;
            }

            // 선택한 문의 유형 텍스트에 따른 유형 코드 추출
            String inquiryCode = InquiryType.getInquiryTypeData(reqData.getType());

            // 문의(상담) 등록
            int nRet = inquiryService.insertInquiry(couponInfoVO.getCustomerId(), reqData.getInquiryTitle(), reqData.getInquiryContent(), inquiryCode, reqData.getMemberId());

            // 문의(상담) 등록 시 실패 int 값을 반환할 경우
            if (nRet <= 0) {
                // 에러 메세지 등록 및 등록 실패 로그 세팅
                LogUtil.logError("insert error. {}", reqData);
                result.setErrorMsg("문의 등록 실패");
                return result;
            }

            // 문의(상담) 등록 성공 메세지 세팅 후 반환
            result.setRsltMsge("문의 등록 성공");

            return result;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setException(ex);
            return result;
        }
    }


    // 내가 작성한 문의 리스트 호출
    @PostMapping(value = "/getInquiryList.do")
    @ResponseBody
    public ResultVO getInquiryList(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody InquiryReqListVO reqData) throws Exception {

        ResultObjVO<Object> result = new ResultObjVO<>();

        try {
            // 암호화된 데이터 복호화
            CouponInfoVO couponInfoVO = kbCouponService.decodeCouponInfo(reqData.getResultText());
            // 복호화된 회원 및 고객사 정보 검증 예외 처리
            String strRet = encodeDataProve(couponInfoVO);

            if(!StringUtil.isNull(strRet)){
                LogUtil.logError(strRet, request, reqData);
                result.setErrorMsg("회원 및 고객사 인증 정보 에러");
                return result;
            }

            // 작성 문의 리스트 호출
            HashMap<String, Object> rspData = inquiryService.getInquiryList(
                    couponInfoVO.getCustomerId(), Integer.parseInt(reqData.getMonth()), Long.parseLong(reqData.getPage()), couponInfoVO.getCorpCode());

            // 문의 데이터 자체가 존재하지 않을 경우
            if (rspData == null) {
                // 에러 코드 세팅
                result.setErrorCode(AppErrorCode.DATA_NOT_FOUND);
                return result;
            }

            // 정상적으로 호출된 문의 리스트를 세팅
            result.setRspList((List<Object>) rspData.get("inquiryList"));
            result.setRspObj((Long) rspData.get("inquiryCount"));

            return result;

        } catch (Exception ex) {

            LogUtil.logException(ex, request);
            result.setException(ex);

            return result;
        }

    }


    // 문의 상세 내용 조회
    @GetMapping(value = "/getInquiryDetail.do")
    @ResponseBody
    public ResultVO getInquiryDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "inquirySeq") Long inquirySeq,
            @RequestParam(value = "memberId") String memberId) throws Exception {

        ResultObjVO<InquiryDetailVO> result = new ResultObjVO<>();

        try {
            // 상세 문의 내용 조회
            InquiryDetailVO rspData = inquiryService.getInquiryDetail(inquirySeq, memberId);

            // 문의 데이터가 존재하지 않을 경우
            if (rspData == null) {
                // 에러 코드 세팅
                result.setErrorCode(AppErrorCode.DATA_NOT_FOUND);
                return result;
            }

            // 정상적으로 호출된 문의 상세 내용을 세팅
            result.setRspObj(rspData);
            return result;

        } catch (Exception ex) {

            LogUtil.logException(ex, request);
            result.setException(ex);

            return result;
        }

    }

}
