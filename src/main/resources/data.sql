drop table if exists categories;
create table categories
(
    id          bigint auto_increment
        primary key,
    create_user varchar(255) null,
    create_date datetime     null,
    update_user varchar(255) null,
    update_date datetime     null,
    name        varchar(255) null,
    description varchar(255) null
);

drop table if exists posts;
create table posts
(
    id           bigint auto_increment
        primary key,
    content      varchar(255) not null,
    description  varchar(255) not null,
    title        varchar(255) not null,
    create_user  varchar(255) null,
    create_date  datetime(6)  null,
    update_date  datetime(6)  null,
    update_user  varchar(255) null,
    category_id  bigint       null,
    author_email varchar(255) null,
    author_name  varchar(255) null,
    constraint UKmchce1gm7f6otpphxd6ixsdps
        unique (title),
    constraint FKijnwr3brs8vaosl80jg9rp7uc
        foreign key (category_id) references categories (id)
);

drop table if exists comments;
create table comments
(
    id          bigint auto_increment
        primary key,
    body        varchar(255) null,
    email       varchar(255) null,
    name        varchar(255) null,
    post_id     bigint       not null,
    create_user varchar(255) null,
    create_date datetime(6)  null,
    update_date datetime(6)  null,
    update_user varchar(255) null,
    constraint FKh4c7lvsc298whoyd4w9ta25cr
        foreign key (post_id) references posts (id)
);

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
);

drop table if exists user_group;
create table user_group
(
    id           bigint auto_increment comment '群組id'
        primary key,
    group_name   varchar(255) not null comment '群組名稱',
    description  varchar(255) null comment '群組描述',
    review_level tinyint      null,
    user_id      bigint       null comment '外件關聯使用者表',
    create_user  varchar(255) null,
    create_date  datetime(6)  null,
    update_date  datetime(6)  null,
    update_user  varchar(255) null
)
    comment '使用者群組';

drop table if exists users;
create table users
(
    id               bigint auto_increment
        primary key,
    email            varchar(255) null,
    name             varchar(255) null,
    password         varchar(255) null,
    username         varchar(255) null,
    create_user      varchar(255) null,
    create_date      datetime(6)  null,
    update_date      datetime(6)  null,
    update_user      varchar(255) null,
    user_group_po_id bigint       null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username),
    constraint FKpr57pp84p91ro585pwss0v73j
        foreign key (user_group_po_id) references user_group (id)
);

alter table user_group
    add constraint user_group_users_id_fk
        foreign key (user_id) references users (id);

drop table if exists user_roles;
create table user_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint FKh8ciramu9cc9q3qcqiv4ue8a6
        foreign key (role_id) references roles (id),
    constraint FKhfh9dx7w3ubf1co1vdev94g3f
        foreign key (user_id) references users (id)
);

