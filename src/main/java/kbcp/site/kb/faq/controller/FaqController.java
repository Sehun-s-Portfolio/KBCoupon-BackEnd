package kbcp.site.kb.faq.controller;

import kbcp.common.base.AbstractController;
import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.LogUtil;
import kbcp.common.vo.ResultObjVO;
import kbcp.common.vo.ResultVO;
import kbcp.site.kb.faq.service.FaqService;
import kbcp.site.kb.faq.vo.FaqListVO;
import kbcp.site.kb.faq.vo.FaqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/faq")
public class FaqController extends AbstractController {

    @Autowired
    private FaqService faqService;


    // 특정 FAQ 상세 조회
    @RequestMapping(value = "/getFaq.do")
    @ResponseBody
    public ResultVO getFaq(HttpServletRequest request, HttpServletResponse response, @RequestParam("faqSeq") Long faqSeq) throws Exception {

        ResultObjVO<FaqVO> result = new ResultObjVO<>();

        try {
            // 특정 FAQ 호출
            FaqVO rspData = faqService.getFaq(faqSeq);

            // FAQ가 존재하지 않을 경우
            if(rspData == null) {
                // 에러 코드 세팅
                result.setErrorCode(AppErrorCode.DATA_NOT_FOUND);
                return result;
            }

            // 정상적으로 호출된 특정 FAQ 세팅
            result.setRspObj(rspData);
            return result;

        } catch (Exception ex) {
            LogUtil.logException(ex, request);
            result.setException(ex);
            return result;
        }

    }


    // FAQ 리스트 호출
    @RequestMapping(value = "/getFaqList.do")
    @ResponseBody
    public ResultVO getFaqList(HttpServletRequest request, HttpServletResponse response, @RequestParam String memberId) throws Exception {

        ResultObjVO<FaqListVO> result = new ResultObjVO<>();

        try {
            // 전체 FAQ 리스트 호출
            List<FaqListVO> rspData = faqService.getFaqList(memberId);

            // FAQ 데이터가 존재하지 않을 경우
            if(rspData == null) {
                // 에러 코드 세팅
                result.setErrorCode(AppErrorCode.DATA_NOT_FOUND);
                return result;
            }

            // 정상적으로 호출된 FAQ 리스트를 세팅
            result.setRspList(rspData);
            return result;

        } catch (Exception ex) {

            LogUtil.logException(ex, request);
            result.setException(ex);

            return result;
        }

    }

}
