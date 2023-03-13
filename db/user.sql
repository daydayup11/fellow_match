-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(255)                    null,
    userAccount  varchar(255)                    null comment '账号',
    avatarUrl    varchar(1024)                   null,
    gender       tinyint                         null,
    userPassword varchar(255)                    not null,
    phone        varchar(255)                    null,
    email        varchar(255)                    null,
    userStatus   int unsigned zerofill default 0 not null,
    createTime   datetime                        null on update CURRENT_TIMESTAMP,
    updateTime   datetime                        null on update CURRENT_TIMESTAMP,
    isDelete     tinyint               default 0 null,
    userRole     int                   default 0 null comment '0-普通用户，1-管理员'
);

