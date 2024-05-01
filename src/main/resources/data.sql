drop table if exists categories;
create table categories
(
    id          bigint auto_increment
        primary key,
    name        varchar(255) null,
    description varchar(255) null,
    create_date datetime     null,
    update_date datetime     null,
    create_user varchar(255) null,
    update_user varchar(255) null,
    is_deleted  bit          not null
) comment '文章分類';

insert into categories (name, description,is_deleted) values ('category1', 'category1 description',false);
insert into categories (name, description,is_deleted) values ('category2', 'category2 description',false);

drop table if exists posts_history;
CREATE TABLE post_history
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    post_id      BIGINT                NOT NULL,
    title        VARCHAR(255)          NOT NULL,
    content      VARCHAR(255)          NULL,
    author_name  VARCHAR(255)          NULL,
    author_email VARCHAR(255)          NULL,
    primary key (id),
    CONSTRAINT posts_history_post_id_fk FOREIGN KEY (post_id) REFERENCES posts (id)
) comment '文章歷史紀錄';


drop table if exists posts;
create table posts
(
    id           bigint auto_increment
        primary key,
    content      varchar(255) not null,
    description  varchar(255) not null,
    title        varchar(255) not null,
    create_date datetime     null,
    update_date datetime     null,
    create_user varchar(255) null,
    update_user varchar(255) null,
    is_deleted  bit          not null,
    category_id  bigint       null,
    author_email varchar(255) null,
    author_name  varchar(255) null,
    views        bigint       null,
    likes        bigint       null,
    constraint unique_title
        unique (title),
    constraint posts_category_id_fk
        foreign key (category_id) references categories (id)
) comment '文章';

insert into posts (content, description, title, category_id, author_email, author_name,is_deleted) values ('content1', 'description1', 'title1', 1, 'email1', 'name1',false);
insert into posts (content, description, title, category_id, author_email, author_name,is_deleted) values ('content2', 'description2', 'title2', 2, 'email2', 'name2',false);

drop table if exists comments;
create table comments
(
    id          bigint auto_increment
        primary key,
    body        varchar(255) null,
    email       varchar(255) null,
    name        varchar(255) null,
    post_id     bigint       not null,
    user_id     bigint       null,
    create_date datetime     null,
    update_date datetime     null,
    create_user varchar(255) null,
    update_user varchar(255) null,
    is_deleted  bit          not null,
    constraint comments_posts_id_fk
        foreign key (post_id) references posts (id),
    constraint comments_users_id_fk
        foreign key (user_id) references users (id)
) comment '留言';

drop table if exists roles;
create table roles
(
    id          bigint auto_increment
        primary key,
    name        varchar(60)  null,
    create_user varchar(255) null,
    create_date datetime(6)  null,
    update_date datetime(6)  null,
    update_user varchar(255) null
) comment '角色表';

drop table if exists user_group;
create table user_group
(
    id           bigint auto_increment comment '群組id'
        primary key,
    group_name   varchar(255) not null comment '群組名稱',
    description  varchar(255) null comment '群組描述',
    review_level tinyint      null,
    user_id      bigint       null comment '外件關聯使用者表',
    create_date datetime     null,
    update_date datetime     null,
    create_user varchar(255) null,
    update_user varchar(255) null,
    is_deleted  bit          not null,
    constraint user_group_users_id_fk
        foreign key (user_id) references users (id)
) comment '使用者群組';

drop table if exists users;
create table users
(
    id               bigint auto_increment
        primary key,
    email            varchar(255) null,
    name             varchar(255) null,
    password         varchar(255) null,
    username         varchar(255) null,
    create_date datetime     null,
    update_date datetime     null,
    create_user varchar(255) null,
    update_user varchar(255) null,
    is_deleted  bit          not null,
    user_group_po_id bigint       null,
    constraint unique_email
        unique (email),
    constraint unique_username
        unique (username),
    constraint user_group_users_id_fk
        foreign key (user_group_po_id) references user_group (id)
        on update cascade on delete cascade
) comment '使用者表';

drop table if exists user_roles;
create table user_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint user_role_role_id_fk
        foreign key (role_id) references roles (id),
    constraint user_role_user_id_fk
        foreign key (user_id) references users (id)
) comment '使用者角色關聯表';

drop table if exists subscription;
CREATE TABLE subscription
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    user_id     BIGINT                NULL,
    post_id     BIGINT                NULL,
    author_name VARCHAR(100)          NULL,
    email       VARCHAR(100)          NULL,
    CONSTRAINT pk_subscription PRIMARY KEY (id),
    CONSTRAINT subscription_post_id_fk FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT subscription_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
) comment '訂閱';

drop table if exists login_history;
CREATE TABLE login_history
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    username     varchar(255)         NULL,
    ip_address  VARCHAR(100)          NULL,
    action      VARCHAR(100)          NULL,
    user_agent  VARCHAR(255)          NULL,
    login_timeStamp  DATETIME         NULL,
    logout_timeStamp DATETIME         NULL
) comment '登入歷史紀錄';

drop table if exists recent_view;
CREATE TABLE recent_view
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    user_id     BIGINT                NULL,
    post_id     BIGINT                NULL,
    CONSTRAINT pk_recent_view PRIMARY KEY (id),
    CONSTRAINT recent_view_post_id_fk FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT recent_view_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
) comment '最近瀏覽紀錄';

drop table if exists mail_notification;
CREATE TABLE mail_notification
(
    id          BIGINT primary key AUTO_INCREMENT NOT NULL,
    content     VARCHAR(255)          NULL,
    email       VARCHAR(100)          NULL,
    is_read     TINYINT               NULL DEFAULT 0,
    name        VARCHAR(100)          NULL,
    send_time   DATETIME              NULL,
    subject     VARCHAR(100)          NULL,
    action      VARCHAR(100)          NULL
) comment '郵件通知';







