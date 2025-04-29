create table if not exists users
(
  id                bigint auto_increment
    primary key,
  first_name        varchar(100)            not null,
  last_name         varchar(100)            not null,
  email             varchar(150)            not null,
  password          varchar(255)            not null,
  phone             varchar(20)             null,
  role              tinytext                not null,
  birthday          date                    null,
  gender            enum ('male', 'female')  null,
  image             varchar(255)            null,
  is_active         boolean                 not null default true,
  is_deleted        boolean                 not null default false,
  registration_date datetime                not null,
  joined_date       datetime                null,
  username          varchar(255)            not null,
  unique (username),
  unique (email)
);

-- TENANTS
create table if not exists tenants
(
  id               bigint auto_increment
    primary key,
  user_id          bigint                      null,
  housing_unit_id  bigint                      null,
  move_in_date     date                        not null,
  move_out_date    date                        null,
  security_deposit decimal(10, 2) default 0.00 null,
  housting_price   decimal(10, 2) default 0.00 null
);

-- HOUSING UNITS
create table if not exists housting_units
(
  id        bigint auto_increment
    primary key,
  number    varchar(20)      not null,
  floor     int              null,
  area      decimal(6, 2)    null,
  address   tinytext         null,
  type      varchar(50)      null,
  tenant_id bigint           null,
  price     double default 0 null
);

create table if not exists invoices
(
  id           bigint auto_increment
    primary key,
  tenant_id    bigint         null,
  type         tinytext       not null,
  month        int            null,
  year         int            null,
  amount       decimal(10, 2) null,
  payment_date date           null,
  status       tinytext       not null,
  check (`month` between 1 and 12)
);

-- SERVICES
CREATE TABLE services
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  code         VARCHAR(50)                           NOT NULL UNIQUE,
  name         VARCHAR(100)                          NOT NULL,
  description  TEXT,
  billing_mode ENUM ('MONTHLY', 'ONE_TIME', 'OTHER') NOT NULL,
  is_active    BOOLEAN DEFAULT TRUE
);

-- SUBSCRIPTIONS
create table if not exists subscriptions
(
  id         bigint auto_increment
    primary key,
  tenant_id  bigint         null,
  service_id bigint         null,
  price      decimal(10, 2) null,
  start_date date           not null,
  end_date   date           null,
  status     tinytext       not null

);

-- BILLING CYCLES
create table if not exists billing_cycles
(
  id              bigint auto_increment
    primary key,
  subscription_id bigint         null,
  amount_due      decimal(10, 2) null,
  period_start    date           not null,
  period_end      date           not null,
  status          tinytext       not null
);

-- payment lines
create table if not exists payment_lines
(
  id               bigint auto_increment primary key,
  payment_id       bigint         null,
  billing_cycle_id bigint         null,
  amount_paid      decimal(10, 2) not null
);

-- ISSUES (COMPLAINTS / INCIDENTS)
create table if not exists issues
(
  id               bigint auto_increment
    primary key,
  tenant_id        bigint                   null,
  title            varchar(150)             not null,
  description      tinytext                 null,
  declaration_date date default (curdate()) null,
  status           tinytext                 not null
);

-- payments
create table if not exists payments
(
  id             bigint auto_increment primary key,
  tenant_id      bigint                   not null,
  payment_date   date default (curdate()) null,
  total_amount   decimal(10, 2)           not null,
  payment_method varchar(50)
);

CREATE TABLE service_options
(
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  service_id BIGINT         NOT NULL,
  name       VARCHAR(255)   NOT NULL,
  price      DECIMAL(10, 2) NOT NULL,
  quantity   INT     DEFAULT 0,
  is_active  BOOLEAN DEFAULT TRUE
);

CREATE TABLE subscription_options
(
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  subscription_id BIGINT NOT NULL,
  option_id       BIGINT NOT NULL,
  quantity        INT DEFAULT 1
);

CREATE TABLE service_usage
(
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  subscription_id BIGINT NOT NULL,
  option_id       BIGINT NOT NULL,
  quantity_used   INT    NOT NULL,
  usage_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs
(
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT        NULL,
  action      VARCHAR(50)   NULL,
  method_name VARCHAR(255)  NULL,
  arguments   TEXT,
  result      TEXT,
  exception   TEXT,
  timestamp   DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- foreign key constraints
alter table subscriptions
  add foreign key (tenant_id) references tenants (id),
  add foreign key (service_id) references services (id);

alter table tenants
  add foreign key (user_id) references users (id),
  add foreign key (housing_unit_id) references housting_units (id);

alter table housting_units
  add foreign key (tenant_id) references tenants (id);

alter table billing_cycles
  add foreign key (subscription_id) references subscriptions (id);

alter table payments
  add foreign key (tenant_id) references tenants (id);

alter table payment_lines
  add foreign key (payment_id) references payments (id),
  add foreign key (billing_cycle_id) references billing_cycles (id);

alter table invoices
  add foreign key (tenant_id) references tenants (id);

alter table issues
  add foreign key (tenant_id) references tenants (id);

alter table service_options
  add FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE;

alter table subscription_options
  add foreign KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE CASCADE,
  add foreign KEY (option_id) REFERENCES service_options (id) ON DELETE CASCADE;

alter table service_usage
  add FOREIGN KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE CASCADE,
  add FOREIGN KEY (option_id) REFERENCES subscription_options (option_id) ON DELETE CASCADE;

ALTER TABLE service_options
  ADD COLUMN quantity INT DEFAULT NULL;

CREATE VIEW billing_cycle_details_view AS
SELECT row_number() OVER () AS `id`,
       bc.id                AS billing_cycle_id,
       bc.period_start      AS start_date,
       bc.period_end        AS end_date,
       bc.amount_due        AS amount_due,
       bc.status            AS billing_cycle_status,
       s.id                 AS subscription_id,
       s.tenant_id          AS subscription_tenant_id,
       s.start_date         AS subscription_start_date,
       s.end_date           AS subscription_end_date,
       sv.id                AS service_id,
       sv.billing_mode      AS service_billing_mode,
       sv.code              AS service_code,
       sv.name              AS service_name,
       sv.description       AS service_description,
       t.id                 AS tenant_id,
       u.id                 AS user_id,
       u.first_name         AS user_first_name,
       u.last_name          AS user_last_name,
       u.username           AS user_username,
       p.id                 AS payment_id,
       p.total_amount       AS payment_total_amount,
       p.payment_date       AS payment_date,
       pl.amount_paid       AS payment_line_amount_paid
FROM billing_cycles bc
       LEFT JOIN
     subscriptions s ON bc.subscription_id = s.id
       LEFT JOIN
     services sv ON s.service_id = sv.id
       LEFT JOIN
     tenants t ON s.tenant_id = t.id
       LEFT JOIN
     users u ON t.user_id = u.id
       LEFT JOIN
     payment_lines pl ON bc.id = pl.billing_cycle_id
       LEFT JOIN
     payments p ON pl.payment_id = p.id;


CREATE VIEW payment_lines_view AS
SELECT row_number() OVER () AS `id`,
       pl.id                AS payment_line_id,
       pl.amount_paid,
       p.id                 AS payment_id,
       p.payment_method     AS payment_method,
       p.total_amount       AS payment_total_amount,
       p.payment_date       AS payment_date,
       t.id                 AS tenant_id,
       t.move_in_date       AS tenant_move_in_date,
       t.move_out_date      AS tenant_move_out_date,
       t.security_deposit   AS tenant_security_deposit,
       us.id                AS user_id,
       us.first_name        AS user_first_name,
       us.last_name         AS user_last_name,
       us.username          AS user_username,
       hu.number            AS housing_unit_number,
       hu.type              AS housing_unit_type,
       bc.id                AS billing_cycle_id,
       bc.amount_due        AS billing_cycle_amount_due,
       bc.status            AS billing_cycle_status,
       s.id                 AS subscription_id,
       s.status             AS subscription_status,
       srv.id               AS service_id,
       srv.code             AS service_code,
       srv.name             AS service_name,
       srv.description      AS service_description,
       srv.billing_mode     AS service_billing_mode,
       srv.is_active        AS service_is_active
FROM payment_lines pl
       LEFT JOIN
     payments p ON pl.payment_id = p.id
       LEFT JOIN
     tenants t ON p.tenant_id = t.id
       LEFT JOIN
     users us ON t.user_id = us.id
       LEFT JOIN
     housting_units hu ON t.housing_unit_id = hu.id
       LEFT JOIN
     billing_cycles bc ON pl.billing_cycle_id = bc.id
       LEFT JOIN
     subscriptions s ON bc.subscription_id = s.id
       LEFT JOIN
     services srv ON s.service_id = srv.id;

CREATE VIEW payments_view AS
SELECT row_number() OVER () AS `id`,
       p.id                 AS payment_id,
       p.payment_method     AS payment_method,
       p.total_amount       AS payment_total_amount,
       p.payment_date       AS payment_date,
       pl.id                AS payment_line_id,
       pl.amount_paid,
       t.id                 AS tenant_id,
       t.move_in_date       AS tenant_move_in_date,
       t.move_out_date      AS tenant_move_out_date,
       t.security_deposit   AS tenant_security_deposit,
       us.id                AS user_id,
       us.first_name        AS user_first_name,
       us.last_name         AS user_last_name,
       us.username          AS user_username,
       hu.number            AS housing_unit_number,
       hu.type              AS housing_unit_type,
       bc.id                AS billing_cycle_id,
       bc.amount_due        AS billing_cycle_amount_due,
       bc.status            AS billing_cycle_status,
       s.id                 AS subscription_id,
       s.status             AS subscription_status,
       srv.id               AS service_id,
       srv.code             AS service_code,
       srv.name             AS service_name,
       srv.description      AS service_description,
       srv.billing_mode     AS service_billing_mode,
       srv.is_active        AS service_is_active
FROM payments p
       LEFT JOIN
     payment_lines pl ON pl.payment_id = p.id
       LEFT JOIN
     tenants t ON p.tenant_id = t.id
       LEFT JOIN
     users us ON t.user_id = us.id
       LEFT JOIN
     housting_units hu ON t.housing_unit_id = hu.id
       LEFT JOIN
     billing_cycles bc ON pl.billing_cycle_id = bc.id
       LEFT JOIN
     subscriptions s ON bc.subscription_id = s.id
       LEFT JOIN
     services srv ON s.service_id = srv.id;
