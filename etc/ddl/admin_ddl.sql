CREATE TABLE account
(
    account_seq       INT     NOT NULL AUTO_INCREMENT COMMENT '계정ID',
    account_type VARCHAR(1) NULL     COMMENT '로그인ID',
    client_seq       INT      NULL COMMENT '고객사ID',
    authorty_seq      INT     NOT NULL COMMENT '권한ID',
    login_id VARCHAR(25) NULL     COMMENT '로그인ID',
    login_password VARCHAR(350) NULL     COMMENT '패스워드',
    account_nm VARCHAR(25) NULL     COMMENT '사용자명',
    phone VARCHAR(350) NULL     COMMENT '2차인증 용 연락처',
    account_st       VARCHAR(1) NULL     COMMENT '사용유무',
    account_memo     VARCHAR(50) NULL     COMMENT '비고',
    create_time timestamp default now()     COMMENT '생성날짜',
    PRIMARY KEY (account_seq)
) COMMENT '계정';

CREATE TABLE authority
(
    authorty_seq        INT      NOT NULL     AUTO_INCREMENT COMMENT '권한ID',
    authorty_nm        VARCHAR(50)  NULL     COMMENT '권한명',
    authorty_st        VARCHAR(1)  NULL     COMMENT '사용유무',
    authorty_memo      VARCHAR(500) NULL     COMMENT '비고',
    authorty_type        INT      NULL     COMMENT '고객사ID',
    create_time timestamp default now()     COMMENT '생성날짜',
    create_user VARCHAR(10)  NULL     COMMENT '생성자',
    update_time timestamp default NULL     COMMENT '수정날짜 ',
    update_user VARCHAR(10)  NULL     COMMENT '수정자',
    PRIMARY KEY (authorty_seq)
) COMMENT '권한테이블';

CREATE TABLE authority_menu
(
    authority_menu_seq      INT     NOT NULL AUTO_INCREMENT COMMENT '권한메뉴ID',
    authorty_seq      INT     NOT NULL COMMENT ' 권한ID',
    authorty_lv      double NULL     COMMENT '메뉴레벨(대분류 -0, 중분류 - 1)',
    authorty_st VARCHAR(1) NULL     COMMENT '노출유무',
    authorty_gb1     VARCHAR(1) NULL     COMMENT '읽기권한',
    authorty_gb2     VARCHAR(1) NULL     COMMENT '등록/수정 권한',
    authorty_gb3     VARCHAR(1) NULL     COMMENT '삭제권한',
    PRIMARY KEY (authority_menu_seq)
) COMMENT '권한 하위 테이블';

CREATE TABLE client
(
    client_seq        INT      NOT NULL     AUTO_INCREMENT COMMENT '고객사ID',
    client_nm        VARCHAR(50)  NULL     COMMENT '고객사명',
    event_id  VARCHAR(50)  NULL     COMMENT '이벤트명ID',
    member_id        VARCHAR(10)  NULL     COMMENT '고객사 식별 ID',
    client_st        VARCHAR(1)  NULL     COMMENT '사용유무',
    create_time timestamp default now()     COMMENT '생성날짜',
    create_user VARCHAR(10)  NULL     COMMENT '생성자',
    update_time timestamp default NULL     COMMENT '수정날짜',
    update_user VARCHAR(10)  NULL     COMMENT '수정자',
    PRIMARY KEY (client_seq)
) COMMENT '고객사';

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


ALTER TABLE authority_menu
    ADD CONSTRAINT fk_authority_to_authority_menu
        FOREIGN KEY (authorty_seq)
            REFERENCES authority (authorty_seq);

ALTER TABLE account
    ADD CONSTRAINT fk_authority_to_account
        FOREIGN KEY (authorty_seq)
            REFERENCES authority (authorty_seq);