create table `event` (
    `event_seq` int not null auto_increment comment '이벤트 seq',
    `event_id` varchar(10) not null  comment '이벤트 id(즐거운 생성)',
    `member_id` varchar(20) not null  comment '고객사 식별id',
    `event_name` varchar(32) not null  comment '프로모션명',
    `expired_date` int not null comment '당첨권 선택가능 기간',
    `top_text` varchar(100) default null comment '상단문구',
    `file_img_nm` varchar(128) default null comment '원본파일명',
    `file_img_path` varchar(150) default null comment '파일상대경로',
    `file_img_key` char(150) collate utf8mb4_bin default null comment 'uuid 파일키',
    `caution` varchar(128) default null comment '유의사항',
    `btn_nm` varchar(10) default null comment '버튼명',
    `btn_color` varchar(15) default null comment '버튼 컬러',
    `create_time` timestamp default now() comment '생성일자',
    `create_user` varchar(64) collate utf8mb4_bin default null comment '생성자',
    `update_time` timestamp default null comment '수정일자',
    `update_user` varchar(64) collate utf8mb4_bin default null comment '수정자',
  primary key (`event_seq`),
  key `ix_event_id` (`event_id`),
  key `ix_member_id` (`member_id`),
  key `ix_event_name` (`event_name`),
  key `ix_update_user` (`update_user`),
  key `ix_update_time` (`update_time` desc)
) engine=innodb auto_increment=101 default charset=utf8mb4 collate=utf8mb4_general_ci comment='이벤트 관리';
