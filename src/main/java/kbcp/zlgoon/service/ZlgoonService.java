package kbcp.zlgoon.service;

import kbcp.common.constant.AppErrorCode;
import kbcp.common.exception.AppException;
import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import kbcp.site.kb.api.mapper.KbApiMapper;
import kbcp.site.kb.coupon.vo.GoodsEventVO;
import kbcp.svc.vo.PrizeVO;
import kbcp.zlgoon.mapper.ZlgoonMapper;
import kbcp.zlgoon.vo.ErrCodeMsgVO;
import kbcp.zlgoon.vo.ReqExchageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@RequiredArgsConstructor
@Service
public class ZlgoonService {

    @Resource(name = "zlgoonMapper")
    private ZlgoonMapper zlgoonMapper;

    // 즐거운 에러코드 메시지 파싱.
    public ErrCodeMsgVO getErrCodeMsg(String strErrCodeMsg) {
        if(StringUtil.isNull(strErrCodeMsg)) {
            LogUtil.logError("strErrCodeMsg null.]");
            return null;
        }

        String[] listRsp = strErrCodeMsg.split(":");
        if(listRsp.length == 1) {
            return new ErrCodeMsgVO(listRsp[0], "");
        } else if(listRsp.length == 2) {
            return new ErrCodeMsgVO(listRsp[0], listRsp[1]);
        }

        LogUtil.logError("invalid strErrCodeMsg. [{}]", strErrCodeMsg);
        return null;
    }

    public int insertExchange(ReqExchageVO reqData) throws Exception {
        return zlgoonMapper.insertExchange(reqData);
    }

    public int updatePrizeExchange(PrizeVO reqData) throws Exception {
        return zlgoonMapper.updatePrizeExchange(reqData);
    }

}
