create table payments
(
    id             bigint auto_increment
        primary key,
    tenant_id      bigint                       not null,
    payment_date   datetime default (curdate()) null,
    total_amount   decimal(10, 2)               not null,
    created_date   datetime                     null,
    updated_date   datetime                     null,
    payment_method varchar(50)                  null,
    constraint payments_ibfk_1
        foreign key (tenant_id) references tenants (id)
);

create index tenant_id
    on payments (tenant_id);

INSERT INTO building.payments (id, tenant_id, payment_date, total_amount, created_date, updated_date, payment_method) VALUES (3, 4, '2025-05-26 01:46:24', 7400.00, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'CASH');
