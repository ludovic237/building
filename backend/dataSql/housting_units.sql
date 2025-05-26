create table housting_units
(
    id           bigint auto_increment
        primary key,
    number       varchar(20)                 not null,
    floor        int                         null,
    area         decimal(6, 2)               null,
    address      tinytext                    null,
    type         varchar(50)                 null,
    tenant_id    bigint                      null,
    created_date datetime                    null,
    updated_date datetime                    null,
    price        decimal(38, 2) default 0.00 null,
    constraint housting_units_ibfk_1
        foreign key (tenant_id) references tenants (id)
);

create index tenant_id
    on housting_units (tenant_id);

INSERT INTO building.housting_units (id, number, floor, area, address, type, tenant_id, created_date, updated_date, price) VALUES (1, 'B 100', 20, 20.00, 'test', 'Chambre', 4, '2025-05-26 01:46:24', null, 1000.00);
INSERT INTO building.housting_units (id, number, floor, area, address, type, tenant_id, created_date, updated_date, price) VALUES (2, 'S 222', 200, 1000.00, 'TEST', 'Studio', null, null, null, 2000.00);
INSERT INTO building.housting_units (id, number, floor, area, address, type, tenant_id, created_date, updated_date, price) VALUES (3, 'A 11', 20, 30.00, 'twest', 'Appartement', null, null, null, 100000.00);
