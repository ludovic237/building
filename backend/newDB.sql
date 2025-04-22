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
  gender            enum ('male', 'female') not null,
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
create table if  not exists issues
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
  tenant_id      bigint         not null,
  payment_date   date default (curdate()) null,
  total_amount   decimal(10, 2) not null,
  payment_method varchar(50)
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




