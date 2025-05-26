create table billing_cycles
(
    id                       bigint auto_increment
        primary key,
    subscription_id          bigint         null,
    amount_due               decimal(10, 2) null,
    period_start             datetime       not null,
    period_end               datetime       not null,
    created_date             datetime       null,
    updated_date             datetime       null,
    status                   tinytext       not null,
    subscription_services_id bigint         null,
    constraint billing_cycles_ibfk_1
        foreign key (subscription_id) references subscriptions (id),
    constraint billing_cycles_ibfk_2
        foreign key (subscription_services_id) references subscription_services (id)
);

create index idx_billing_cycle_id
    on billing_cycles (subscription_id);

create index subscription_services_id
    on billing_cycles (subscription_services_id);

INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (14, 3, 1000.00, '2025-05-26 23:00:00', '2025-06-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (15, 3, 1000.00, '2025-06-26 23:00:00', '2025-07-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (16, 3, 1000.00, '2025-07-26 23:00:00', '2025-08-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (17, 3, 1000.00, '2025-08-26 23:00:00', '2025-09-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (18, 3, 1000.00, '2025-09-26 23:00:00', '2025-10-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (19, 3, 1000.00, '2025-10-26 23:00:00', '2025-11-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (20, 3, 1000.00, '2025-11-26 23:00:00', '2025-12-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (21, 3, 1000.00, '2025-12-26 23:00:00', '2026-01-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PARTIAL_PAID', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (22, 3, 1000.00, '2026-01-26 23:00:00', '2026-02-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (23, 3, 1000.00, '2026-02-26 23:00:00', '2026-03-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (24, 3, 1000.00, '2026-03-26 23:00:00', '2026-04-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3);
INSERT INTO building.billing_cycles (id, subscription_id, amount_due, period_start, period_end, created_date, updated_date, status, subscription_services_id) VALUES (25, 3, 1000.00, '2026-04-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', '2025-05-26 01:46:24', 'PENDING', 3);
