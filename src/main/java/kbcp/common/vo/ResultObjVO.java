package kbcp.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ResultObjVO<T> extends ResultVO {
    private ReqListVO reqData = null;
    private T rspObj = null;
    private List<T> rspList = null;
}
