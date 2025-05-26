create table tenants
(
    id               bigint auto_increment
        primary key,
    user_id          bigint                      null,
    housing_unit_id  bigint                      null,
    move_in_date     datetime                    not null,
    move_out_date    datetime                    null,
    created_date     datetime                    null,
    updated_date     datetime                    null,
    security_deposit decimal(10, 2) default 0.00 null,
    housting_price   decimal(10, 2) default 0.00 null,
    constraint tenants_ibfk_1
        foreign key (user_id) references users (id),
    constraint tenants_ibfk_2
        foreign key (housing_unit_id) references housting_units (id)
);

create index housing_unit_id
    on tenants (housing_unit_id);

create index user_id
    on tenants (user_id);

INSERT INTO building.tenants (id, user_id, housing_unit_id, move_in_date, move_out_date, created_date, updated_date, security_deposit, housting_price) VALUES (4, 1, 1, '2025-05-26 23:00:00', '2026-05-26 23:00:00', '2025-05-26 01:46:24', null, 7400.00, null);
