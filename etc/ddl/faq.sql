CREATE TABLE faq
(
    faq_seq        INT      NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
    faq_title     VARCHAR(50)  NULL     COMMENT '제목',
    faq_content   VARCHAR(500)  NULL     COMMENT '내용',
    faq_st   VARCHAR(1)  NULL     COMMENT '노출유무',
    faq_ord       INT      NULL     COMMENT '노출순서',
    create_time timestamp default now()     COMMENT '생성날짜',
    create_user VARCHAR(10)  NULL     COMMENT '생성자',
    update_time timestamp default NULL     COMMENT '수정날짜',
    update_user VARCHAR(10)  NULL     COMMENT '수정자',
    `member_id`       varchar(20) not null  comment '고객사 식별id(kbstart)',
    PRIMARY KEY (faq_seq),
    KEY               `member_id` (`member_id`)
);
