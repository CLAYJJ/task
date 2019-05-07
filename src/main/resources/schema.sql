#以下的建表语句使用的database名为task。
create table if not exists task(
    id varchar(50) not null ,
    mark_time date,
    content text,
    create_time datetime,
    modify_time datetime,
    primary key (id)
)engine = innodb default charset utf8;