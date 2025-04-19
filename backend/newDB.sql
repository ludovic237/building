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
  birthday          DATE                     NULL,
  gender            ENUM ('male', 'female') NOT NULL,
  image             VARCHAR(255)            NULL,
  is_active         BOOLEAN                 NOT NULL DEFAULT TRUE,
  is_deleted        BOOLEAN                 NOT NULL DEFAULT FALSE,
  registration_date DATETIME                NOT NULL,
  joined_date DATETIME                 NULL,
  username          varchar(255)            not null,
  unique (username),
  unique (email)
);

create table if not exists services
(
  id            bigint auto_increment
    primary key,
  name          varchar(100)   not null,
  description   tinytext       null,
  monthly_price decimal(10, 2) null,
    unique (name)
);

create table if not exists subscriptions
(
  id         bigint auto_increment
    primary key,
  user_id    bigint   null,
  service_id bigint   null,
  start_date date     not null,
  end_date   date     null,
  status     tinytext not null

);

create table if not exists tenants
(
  id               bigint auto_increment
    primary key,
  user_id          bigint                      null,
  housing_unit_id  bigint                      null,
  move_in_date     date                        not null,
  move_out_date    date                        null,
  security_deposit decimal(10, 2) default 0.00 null
);

create table if not exists housting_units
(
  id        bigint auto_increment
    primary key,
  number    varchar(20)   not null,
  floor     int           null,
  area      decimal(6, 2) null,
  address   tinytext      null,
  type      varchar(50)   null,
  tenant_id bigint        null,
  price DOUBLE default 0 null,
  unique (number)
);

create table if not exists rents
(
  id           bigint auto_increment
    primary key,
  tenant_id    bigint         null,
  month        int            null,
  year         int            null,
  amount       decimal(10, 2) not null,
  payment_date date           null,
  status       tinytext       not null
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

-- Foreign key constraints
alter table subscriptions
  add foreign key (user_id) references users (id),
  add foreign key (service_id) references services (id);

alter table tenants
  add foreign key (user_id) references users (id),
  add foreign key (housing_unit_id) references housting_units (id);

alter table housting_units
  add foreign key (tenant_id) references tenants (id);

alter table rents
  add foreign key (tenant_id) references tenants (id);

alter table invoices
  add foreign key (tenant_id) references tenants (id);

alter table issues
  add foreign key (tenant_id) references tenants (id);

CREATE INDEX idx_user_id ON tenants(user_id);
CREATE INDEX idx_tenant_id ON rents(tenant_id);
CREATE INDEX idx_service_id ON subscriptions(service_id);
CREATE INDEX idx_housting_unit_id ON tenants(housing_unit_id);



