create table invoices
(
    id           bigint auto_increment
        primary key,
    user_id      bigint         null,
    modify_id    bigint         null,
    type         tinytext       not null,
    number       tinytext       null,
    month        int            null,
    year         int            null,
    amount       decimal(10, 2) null,
    payment_date datetime       null,
    created_date datetime       null,
    updated_date datetime       null,
    status       tinytext       not null,
    tenant_id    bigint         null,
    constraint invoices_ibfk_1
        foreign key (modify_id) references users (id),
    constraint invoices_ibfk_2
        foreign key (user_id) references users (id),
    check (`month` between 1 and 12)
);

create index modify_id
    on invoices (modify_id);

create index user_id
    on invoices (user_id);

INSERT INTO building.invoices (id, user_id, modify_id, type, number, month, year, amount, payment_date, created_date, updated_date, status, tenant_id) VALUES (3, 1, 1, 'subscription', 'INV-2025-05-00004', 5, 2025, 12000.00, '2025-05-26 01:46:24', '2025-05-26 01:46:24', null, 'PARTIAL_PAID', 4);
