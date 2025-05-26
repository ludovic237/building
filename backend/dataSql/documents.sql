create table documents
(
    id           bigint auto_increment
        primary key,
    user_id      bigint         null,
    name         varchar(255)   null,
    type         varchar(100)   null,
    size         int            null,
    content      varbinary(255) null,
    created_date datetime       null,
    updated_date datetime       null,
    hash         varchar(255)   null,
    constraint FKkxttj4tp5le2uth212lu49vny
        foreign key (user_id) references users (id)
);

