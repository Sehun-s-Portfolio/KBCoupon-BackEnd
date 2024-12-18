package kbcp.common.exception;

import kbcp.common.constant.AppErrorCode;
import kbcp.common.util.LogUtil;
import kbcp.common.util.VoUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppException extends RuntimeException {
	private static final long serialVersionUID = 2063143507320417047L;

    private AppErrorCode enumError = AppErrorCode.SUCCESS;

    public AppException() {
        super();

    }

    public AppException(String message) {
        super(message);
    }

    public AppException(AppErrorCode errCd) {
        super(errCd.getErrMsg());

        this.setEnumError(errCd);
    }

    public String getErrCode() {
        return enumError.getErrCode();
    }
}
