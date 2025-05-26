create table payment_lines
(
    id               bigint auto_increment
        primary key,
    payment_id       bigint         null,
    billing_cycle_id bigint         null,
    created_date     datetime       null,
    updated_date     datetime       null,
    amount_paid      decimal(10, 2) not null,
    constraint payment_lines_ibfk_1
        foreign key (payment_id) references payments (id),
    constraint payment_lines_ibfk_2
        foreign key (billing_cycle_id) references billing_cycles (id)
);

create index idx_payment_line_id
    on payment_lines (billing_cycle_id);

create index payment_id
    on payment_lines (payment_id);

INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (14, 3, 14, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (15, 3, 15, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (16, 3, 16, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (17, 3, 17, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (18, 3, 18, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (19, 3, 19, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (20, 3, 20, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 1000.00);
INSERT INTO building.payment_lines (id, payment_id, billing_cycle_id, created_date, updated_date, amount_paid) VALUES (21, 3, 21, '2025-05-26 01:46:24', '2025-05-26 01:46:24', 400.00);
