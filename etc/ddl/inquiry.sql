CREATE TABLE `inquiry`
(
    `inquiry_seq`     bigint                                  NOT NULL AUTO_INCREMENT comment '문의(상담) ID',
    `inquiry_title`   varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL comment '문의(상담) 타이틀',
    `inquiry_content` longtext COLLATE utf8mb4_unicode_ci     NOT NULL comment '문의(상담) 내용',
    `inquiry_answer`  longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL comment '문의(상담) 답변 내용',
    `create_time`     timestamp                           DEFAULT now() comment '생성 일자',
    `create_user`     varchar(20)                             NOT NULL comment '문의(상담)자 계정 ID',
    `update_time`     timestamp                           DEFAULT NULL comment '수정 일자',
    `update_user`     VARCHAR(10) NULL COMMENT '수정자',
    `type`            char(3) COLLATE utf8mb4_bin             NOT NULL comment '문의(상담) 유형',
    `member_id`       varchar(20) not null  comment '고객사 식별id(kbstart)',
    PRIMARY KEY (`inquiry_seq`),
    KEY               `create_user` (`create_user`),
    KEY               `member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_unicode_ci;

