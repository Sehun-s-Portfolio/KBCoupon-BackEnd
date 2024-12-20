create table `coupon` (
    `coupon_seq` bigint not null auto_increment comment '쿠폰 seq',
    `cmd` char(1) not null  comment '0:쿠폰발행, 1:쿠폰재전송, 3:쿠폰취소',
    `prize_seq` bigint not null comment '당첨권 seq',
    `event_id` varchar(10) not null  comment '이벤트 id(즐거운 생성)',
    `tr_id` varchar(50) not null comment '고객사 tr',
    `member_id` varchar(20) not null  comment '고객사 식별id(kbstart)',
    `goods_id` char(10) default null comment '상품 id(즐거운 생성)',
    `run_batch` char(1) default '0' comment '쿠폰 배치발급여부(0:일반, 1:배치발급)',
    `rsp_code` varchar(20) default null comment '응답코드',
    `rsp_msg` varchar(50) default null comment '응답코드',
    `coupon_num` varchar(30) default null comment '쿠폰번호(즐거운 생성)',
    `order_num` varchar(30) default null comment '주문번호',
    `req_time` timestamp default now() comment '요청일시',
    `rsp_time` timestamp default null comment '응답일시',
  primary key (`coupon_seq`),
  foreign key (`prize_seq`) references `prize` (`prize_seq`),
  key `ix_tr_id` (`tr_id`),
  key `ix_member_id` (`member_id`),
  key `ix_coupon_num` (`coupon_num`),
  key `ix_req_time` (`req_time` desc)
) engine=innodb auto_increment=10001 default charset=utf8mb4 collate=utf8mb4_general_ci comment='쿠폰발급 이력';
