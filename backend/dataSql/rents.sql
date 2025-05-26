create table rents
(
    id             bigint auto_increment
        primary key,
    amount         decimal(10, 2) not null,
    housting_price decimal(10, 2) not null,
    month          int            null,
    payment_date   datetime(6)    null,
    status         tinytext       not null,
    year           int            null,
    tenant_id      bigint         null,
    constraint FKt0syrma55uiuc3uh84wkl095c
        foreign key (tenant_id) references tenants (id)
);

