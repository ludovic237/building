create table subscription_services
(
    id               bigint auto_increment
        primary key,
    subscription_id  bigint                                   not null,
    service_id       bigint                                   not null,
    quantity         int            default 1                 null,
    price            decimal(10, 2) default 0.00              not null,
    total_price      decimal(10, 2) default 0.00              not null,
    subscript_number int                                      null,
    start_date       datetime                                 not null,
    end_date         datetime                                 null,
    created_date     datetime       default CURRENT_TIMESTAMP null,
    updated_date     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    billing_cycle_id bigint                                   null,
    amount_due       decimal(10, 2) default 0.00              not null,
    constraint subscription_services_ibfk_1
        foreign key (subscription_id) references subscriptions (id),
    constraint subscription_services_ibfk_2
        foreign key (service_id) references services (id),
    constraint subscription_services_ibfk_3
        foreign key (billing_cycle_id) references billing_cycles (id)
);

create index billing_cycle_id
    on subscription_services (billing_cycle_id);

create index idx_subscription_id
    on subscription_services (subscription_id);

create index service_id
    on subscription_services (service_id);

INSERT INTO building.subscription_services (id, subscription_id, service_id, quantity, price, total_price, subscript_number, start_date, end_date, created_date, updated_date, billing_cycle_id, amount_due) VALUES (3, 3, 1, 12, 1000.00, 12000.00, 12, '2025-05-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', null, 12000.00);
