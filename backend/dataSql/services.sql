create table services
(
    id           bigint auto_increment
        primary key,
    code         varchar(50)                 null,
    name         varchar(100)                not null,
    type         varchar(100)                not null,
    description  tinytext                    null,
    billing_mode tinytext                    null,
    price        decimal(38, 2) default 0.00 null,
    created_date datetime                    null,
    updated_date datetime                    null,
    is_active    tinyint(1)     default 1    null,
    constraint code
        unique (code)
);

INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (1, 'ww', 'loyer', 'PERIODIC', 'dsds', 'monthly', null, null, null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (10, '0001', 'laverie', 'PERIODIC_WITH_OPTIONS', 'tes', 'monthly', 0.00, '2025-05-19 19:18:11', '2025-05-19 19:18:11', 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (17, null, 'Internet Service', 'PERIODIC', null, 'monthly', 50.00, null, null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (18, null, 'Cleaning Service', 'PERIODIC', null, 'monthly', 30.00, null, null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (19, null, 'Gym Membership', 'PERIODIC_WITH_OPTIONS', null, 'monthly', 20.00, null, null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (20, null, 'Streaming Service', 'PERIODIC_WITH_OPTIONS', null, 'monthly', 10.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (21, null, 'Basic Water Supply Service', 'PERIODIC', null, 'monthly', 25.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (22, null, 'Parking Space Rental', 'PERIODIC', null, 'monthly', 50.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (23, null, 'Cleaning Service', 'PERIODIC', null, 'monthly', 30.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (24, null, 'Swimming Pool Access', 'PERIODIC', null, 'seasonal', 100.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (25, null, 'Home Security Service', 'PERIODIC_WITH_OPTIONS', null, 'monthly', 40.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (26, null, 'Premium Internet Service', 'PERIODIC', null, 'monthly', 60.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (27, null, 'Home Cleaning Service', 'OPTIONS', null, 'per-session', 50.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (28, null, 'Magazine Subscription', 'PERIODIC', null, 'monthly', 10.00, '2025-05-20 14:20:19', null, 1);
INSERT INTO building.services (id, code, name, type, description, billing_mode, price, created_date, updated_date, is_active) VALUES (29, null, 'Gym Membership', 'PERIODIC', null, 'monthly', 20.00, '2025-05-20 14:20:19', null, 1);
