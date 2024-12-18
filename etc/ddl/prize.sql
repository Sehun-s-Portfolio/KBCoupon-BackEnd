create table `prize` (
    `prize_seq` bigint not null auto_increment comment '당첨권 seq',
    `event_seq` int not null comment '이벤트 seq',
    `goods_seq` bigint not null comment '상품 seq',
    `goods_id` varchar(15) not null  comment '5(난수)+10(상품ID)',
    `goods_seq_before` bigint default null comment '변경전 상품 seq',
    `tran_id` varchar(50) not null comment '고객사 tr',
    `corp_code` varchar(20) not null  comment '고객사 식별id(kbstar)',
    `customer_id` varchar(20) collate utf8mb4_bin not null comment '고객id',
    `goods_id_no` varchar(20) not null comment '상품코드 순번(kb에서 발급)',
    `req_code` char(2) default null comment '10:발급 요청, 20:재발급 요청',
    `run_batch` char(1) default '0' comment '쿠폰 배치발급여부(0:일반, 1:배치발급)',
    `coupon_num` varchar(30) default null comment '쿠폰번호(즐거운 생성)',
    `coupon_time` timestamp default null  comment '쿠폰발급 요청/응답시간',
    `coupon_expired_date` char(8) default null comment '쿠폰 만료일',
    `coupon_status` char(2) default '02' comment '01:사용, 02:미사용',
    `coupon_use_time` timestamp default null  comment '쿠폰 사용시간',
    `status` char(1) default '0' comment '쿠폰상태(0:당첨권 접수, 1:쿠폰발급완료, 2:교환완료, 3:교환취소, 4:알수없음, 9:삭제)',
    `create_time` timestamp default now() comment '생성일자',
    `update_time` timestamp default null comment '수정일자',
    `update_user` varchar(64) collate utf8mb4_bin default null comment '수정자',
  primary key (`prize_seq`),
  foreign key (`event_seq`) references `event` (`event_seq`),
  foreign key (`goods_seq`) references `goods` (`goods_seq`),
  foreign key (`goods_seq_before`) references `goods` (`goods_seq`),
  unique key `uk_goods_id_no` (`goods_id_no`),
  key `ix_tran_id` (`tran_id`),
  key `ix_customer_id` (`customer_id`),
  key `ix_coupon_num` (`coupon_num`),
  key `ix_create_time` (`create_time` desc),
  key `ix_update_time` (`update_time` desc)
) engine=innodb auto_increment=10001 default charset=utf8mb4 collate=utf8mb4_general_ci comment='당첨권/쿠폰 관리';
