package kbcp.zlgoon.vo;

import kbcp.common.base.AbstractVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqExchageVO extends AbstractVO {
    private String exchange_seq;// seq 번호
    private String ver; 	    // v10
    private String barcode_num; // 쿠폰번호(최대30자리)
    private String exchange_type; 	// 1:교환, 3:교환취소
    private String coupon_status; 	// 0:교환대기(주문 취소가 가능한 상태), 1:교환(주문 취소가 불가능한 상태)
    private String use_balance; 	// 사용금액(교환 및 교환 취소 금액)
    private String order_balance;   // 잔액 (해당 교환건 처리 후 남은 잔액)
    private String product_type;    // BARCODE:기본상품, AMOUNT:잔액관리권
    private String branch_code;     // 지점코드
    private String branch_name;     // 지점명
    private String exchange_num;    // 교환번호: 20150128101006054169(교환취소일 경우 원거래교환번호와 동일)
    private String exchange_date;   // 20150128101006
    private String tr_id;           // PUSH_ID 내역 전달 id
    private String date_time;       // 전송일시
    private String errmessage;      // 에러메시지
    private String res_code;        // 성공 : 0000, 실패 : 9999
    private String create_time;     // 생성일시
}

