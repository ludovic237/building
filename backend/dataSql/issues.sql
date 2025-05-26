create table issues
(
    id               bigint auto_increment
        primary key,
    tenant_id        bigint                       null,
    title            varchar(150)                 not null,
    description      tinytext                     null,
    declaration_date datetime default (curdate()) null,
    created_date     datetime                     null,
    updated_date     datetime                     null,
    status           tinytext                     not null,
    constraint issues_ibfk_1
        foreign key (tenant_id) references tenants (id)
);

create index tenant_id
    on issues (tenant_id);

