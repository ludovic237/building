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
  birthday          datetime                null,
  gender            enum ('male', 'female') null,
  image             varchar(255)            null,
  is_active         boolean                 not null default true,
  is_deleted        boolean                 not null default false,
  registration_date datetime                not null,
  joined_date       datetime                null,
  created_date      datetime                null,
  updated_date      datetime                null,
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
  move_in_date     datetime                    not null,
  move_out_date    datetime                    null,
  created_date     datetime                    null,
  updated_date     datetime                    null,
  security_deposit decimal(10, 2) default 0.00 null,
  housting_price   decimal(10, 2) default 0.00 null
);

-- HOUSING UNITS
create table if not exists housting_units
(
  id           bigint auto_increment
    primary key,
  number       varchar(20)      not null,
  floor        int              null,
  area         decimal(6, 2)    null,
  address      tinytext         null,
  type         varchar(50)      null,
  tenant_id    bigint           null,
  created_date datetime         null,
  updated_date datetime         null,
  price        double default 0 null
);

create table if not exists invoices
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
  check (`month` between 1 and 12)
);

-- SERVICES
CREATE TABLE services
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  code         VARCHAR(50)    NULL UNIQUE,
  name         VARCHAR(100)   NOT NULL,
  type         VARCHAR(100)   NOT NULL,
  description  TEXT,
  billing_mode tinytext       NULL,
  price        decimal(10, 2) null,
  created_date datetime       null,
  updated_date datetime       null,
  is_active    BOOLEAN DEFAULT TRUE
);

-- OPTIONS
CREATE TABLE service_options
(
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  service_id   BIGINT         NOT NULL,
  name         VARCHAR(255)   NOT NULL,
  price        DECIMAL(10, 2) NOT NULL,
  max_quantity INT     DEFAULT 0,
  is_active    BOOLEAN DEFAULT TRUE,
  created_date datetime       null,
  updated_date datetime       null
);

-- SUBSCRIPTIONS
create table if not exists subscriptions
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
  status           tinytext       not null

);

-- SUBSCRIPTION SERVICEs
CREATE TABLE subscription_services
(
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  subscription_id  BIGINT   NOT NULL,
  service_id       BIGINT   NOT NULL,
  quantity         INT            DEFAULT 1,
  price            DECIMAL(10, 2) DEFAULT 0.00,
  subscript_number int      null,
  start_date       datetime not null,
  end_date         datetime null,
  created_date     DATETIME       DEFAULT CURRENT_TIMESTAMP,
  updated_date     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- subscription options
create table subscription_options
(
  id                      bigint primary key auto_increment,
  subscription_service_id bigint         not null,
  subscription_id bigint         not null,
  option_id               bigint         not null,
  quantity                int default 1,
  price                   decimal(10, 2) null, -- prix total pour cette option (quantity * option.price)
  created_date            datetime       null,
  updated_date            datetime       null
);

-- BILLING CYCLES
create table if not exists billing_cycles
(
  id              bigint auto_increment
    primary key,
  subscription_id bigint         null,
  subscription_services_id bigint         null,
  amount_due      decimal(10, 2) null,
  period_start    datetime       not null,
  period_end      datetime       not null,
  created_date    datetime       null,
  updated_date    datetime       null,
  status          tinytext       not null
);

-- payment lines
create table if not exists payment_lines
(
  id               bigint auto_increment primary key,
  payment_id       bigint         null,
  billing_cycle_id bigint         null,
  created_date     datetime       null,
  updated_date     datetime       null,
  amount_paid      decimal(10, 2) not null
);

-- ISSUES (COMPLAINTS / INCIDENTS)
create table if not exists issues
(
  id               bigint auto_increment
    primary key,
  tenant_id        bigint                       null,
  title            varchar(150)                 not null,
  description      tinytext                     null,
  declaration_date datetime default (curdate()) null,
  created_date     datetime                     null,
  updated_date     datetime                     null,
  status           tinytext                     not null
);

-- payments
create table if not exists payments
(
  id             bigint auto_increment primary key,
  tenant_id      bigint                       not null,
  payment_date   datetime default (curdate()) null,
  total_amount   decimal(10, 2)               not null,
  created_date   datetime                     null,
  updated_date   datetime                     null,
  payment_method varchar(50)
);

CREATE TABLE service_usage
(
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  subscription_id BIGINT   NOT NULL,
  option_id       BIGINT   NOT NULL,
  quantity_used   INT      NOT NULL,
  usage_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_date    datetime null,
  updated_date    datetime null
);

CREATE TABLE audit_logs
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT       NULL,
  action       VARCHAR(50)  NULL,
  method_name  VARCHAR(255) NULL,
  arguments    longtext,
  result       LONGTEXT,
  exception    longtext,
  timestamp    DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_date datetime     null,
  updated_date datetime     null
);


CREATE TABLE documents
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      bigint       null,
  name         VARCHAR(255) NULL,
  type         VARCHAR(100) NULL,
  size         int          NULL,
  content      BLOB,
  created_date datetime     null,
  updated_date datetime     null
);

CREATE TABLE invoice_counter
(
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  year    int NULL,
  counter int NULL
);


-- foreign key constraints
alter table subscriptions
  add foreign key (tenant_id) references tenants (id),
  add foreign key (update_by) references users (id),
  add foreign key (invoice_id) references invoices (id),
  add foreign key (service_id) references services (id);

-- foreign key constraints
alter table subscription_services
  add foreign key (subscription_id) references subscriptions (id),
  add foreign key (service_id) references services (id);

-- foreign key constraints
alter table subscription_options
  add foreign key (subscription_service_id) references subscription_services (id),
  add foreign key (option_id) references service_options (id);

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
  add foreign key (modify_id) references users (id),
  add foreign key (user_id) references users (id);

alter table issues
  add foreign key (tenant_id) references tenants (id);

alter table billing_cycles
  add foreign key (subscription_services_id) references subscription_services (id);

alter table service_options
  add FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE;


alter table service_usage
  add FOREIGN KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE CASCADE,
  add FOREIGN KEY (option_id) REFERENCES subscription_options (option_id) ON DELETE CASCADE;

alter table documents
  add user_id bigint null,
  add hash    blob   null,
  add foreign key (user_id) references users (id);

ALTER TABLE service_options
  ADD COLUMN quantity INT DEFAULT NULL;


ALTER TABLE subscription_services
  ADD COLUMN billing_cycle_id BIGINT NULL,
  ADD FOREIGN KEY (billing_cycle_id) REFERENCES billing_cycles (id);

ALTER TABLE subscription_options
  ADD FOREIGN KEY (subscription_id) REFERENCES subscriptions (id);

ALTER TABLE subscription_services
  ADD COLUMN amount_due DECIMAL(10, 2) DEFAULT 0.00;

ALTER TABLE subscription_options
  ADD COLUMN amount_due DECIMAL(10, 2) DEFAULT 0.00;

CREATE INDEX idx_subscription_id ON subscription_services (subscription_id);
CREATE INDEX idx_subscription_service_id ON subscription_options (subscription_service_id);
CREATE INDEX idx_billing_cycle_id ON billing_cycles (subscription_id);
CREATE INDEX idx_payment_line_id ON payment_lines (billing_cycle_id);

alter table subscription_options
  modify price decimal(38, 2) null;

alter table subscription_options
  modify subscription_id bigint null;

alter table subscription_options
  modify amount_due decimal(10, 2) default 0.00 null;

alter table subscription_services
  add total_price decimal(10, 2) default 0.00 not null after price;

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

CREATE DEFINER = root@localhost VIEW payments_simple_view AS
SELECT *
FROM (SELECT ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY p.payment_date DESC) AS row_num,
             p.id                                                               AS id,
             p.id                                                               AS payment_id,
             p.payment_method                                                   AS payment_method,
             p.total_amount                                                     AS payment_total_amount,
             p.payment_date                                                     AS payment_date,
             pl.id                                                              AS payment_line_id,
             pl.amount_paid,
             t.id                                                               AS tenant_id,
             t.move_in_date                                                     AS tenant_move_in_date,
             t.move_out_date                                                    AS tenant_move_out_date,
             t.security_deposit                                                 AS tenant_security_deposit,
             us.id                                                              AS user_id,
             us.first_name                                                      AS user_first_name,
             us.last_name                                                       AS user_last_name,
             us.username                                                        AS user_username,
             hu.number                                                          AS housing_unit_number,
             hu.type                                                            AS housing_unit_type,
             bc.id                                                              AS billing_cycle_id,
             bc.amount_due                                                      AS billing_cycle_amount_due,
             bc.status                                                          AS billing_cycle_status,
             s.id                                                               AS subscription_id,
             s.status                                                           AS subscription_status,
             srv.id                                                             AS service_id,
             srv.code                                                           AS service_code,
             srv.name                                                           AS service_name,
             srv.description                                                    AS service_description,
             srv.billing_mode                                                   AS service_billing_mode,
             srv.is_active                                                      AS service_is_active
      FROM payments p
             LEFT JOIN payment_lines pl ON pl.payment_id = p.id
             LEFT JOIN tenants t ON p.tenant_id = t.id
             LEFT JOIN users us ON t.user_id = us.id
             LEFT JOIN housting_units hu ON t.housing_unit_id = hu.id
             LEFT JOIN billing_cycles bc ON pl.billing_cycle_id = bc.id
             LEFT JOIN subscriptions s ON bc.subscription_id = s.id
             LEFT JOIN subscription_services ss ON ss.subscription_id = s.id
             LEFT JOIN services srv ON ss.service_id = srv.id) subquery
WHERE row_num = 1;

CREATE VIEW subscription_payment_summary AS
SELECT s.id                                               AS subscription_id,
       ss.id                                              AS subscription_service_id,
       so.id                                              AS subscription_option_id,
       s.total_price                                      AS subscription_total_price,
       ss.price                                           AS service_price,
       so.price                                           AS option_price,
       COALESCE(SUM(pl.amount_paid), 0)                   AS total_paid,
       (s.total_price - COALESCE(SUM(pl.amount_paid), 0)) AS remaining_balance
FROM subscriptions s
       LEFT JOIN subscription_services ss ON ss.subscription_id = s.id
       LEFT JOIN subscription_options so ON so.subscription_service_id = ss.id
       LEFT JOIN payment_lines pl ON pl.billing_cycle_id = ss.billing_cycle_id
GROUP BY s.id, ss.id, so.id;
