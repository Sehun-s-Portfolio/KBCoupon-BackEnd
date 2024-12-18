package kbcp.scheduler.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RunCntVO extends AbstractVO {
    private int total=0;    // 전체
    private int success=0;  // 성공
    private int fail=0;     // 실패

    public int addTotal() {
        return ++this.total;
    }

    public int addSuccess() {
        return ++this.success;
    }

    public int addFail() {
        return ++this.fail;
    }
}
