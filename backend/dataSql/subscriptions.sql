create table subscriptions
(
    id               bigint auto_increment
        primary key,
    tenant_id        bigint         null,
    update_by        bigint         null,
    invoice_id       bigint         null,
    service_id       bigint         null,
    total_price      decimal(10, 2) null,
    start_date       datetime       null,
    end_date         datetime       null,
    subscript_number int            null,
    created_date     datetime       null,
    updated_date     datetime       null,
    status           tinytext       not null,
    modify_by        bigint         null,
    constraint FKeb88oafdf8xfo8j1kvle8r05l
        foreign key (modify_by) references users (id),
    constraint subscriptions_ibfk_1
        foreign key (tenant_id) references tenants (id),
    constraint subscriptions_ibfk_2
        foreign key (update_by) references users (id),
    constraint subscriptions_ibfk_3
        foreign key (invoice_id) references invoices (id),
    constraint subscriptions_ibfk_4
        foreign key (service_id) references services (id)
);

create index invoice_id
    on subscriptions (invoice_id);

create index service_id
    on subscriptions (service_id);

create index tenant_id
    on subscriptions (tenant_id);

create index update_by
    on subscriptions (update_by);

INSERT INTO building.subscriptions (id, tenant_id, update_by, invoice_id, service_id, total_price, start_date, end_date, subscript_number, created_date, updated_date, status, modify_by) VALUES (3, 4, null, 3, null, 12000.00, '2025-05-26 23:00:00', '2026-05-26 23:00:00', 12, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'ACTIVE', 1);
