create table if not exists building.housting_units
(
  id      bigint auto_increment
  primary key,
  number  varchar(20)   not null,
  floor   int           null,
  area    decimal(6, 2) null,
  address tinytext      null,
  type    varchar(50)   null,
  constraint number
  unique (number)
  );

create table if not exists building.services
(
  id            bigint auto_increment
  primary key,
  name          varchar(100)   not null,
  description   tinytext       null,
  monthly_price decimal(10, 2) null,
  constraint name
  unique (name)
  );

create table if not exists building.users
(
  id         bigint auto_increment
  primary key,
  first_name varchar(100) not null,
  last_name  varchar(100) not null,
  email      varchar(150) not null,
  password   varchar(255) not null,
  phone      varchar(20)  null,
  role       tinytext     not null,
  username   varchar(255) not null,
  constraint UKr43af9ap4edm43mmtq01oddj6
  unique (username),
  constraint email
  unique (email)
  );

create table if not exists building.subscriptions
(
  id         bigint auto_increment
  primary key,
  user_id    bigint   null,
  service_id bigint   null,
  start_date date     not null,
  end_date   date     null,
  status     tinytext not null,
  constraint subscriptions_ibfk_1
  foreign key (user_id) references building.users (id),
  constraint subscriptions_ibfk_2
  foreign key (service_id) references building.services (id)
  );

create index idx_service_id
  on building.subscriptions (service_id);

create index user_id
  on building.subscriptions (user_id);

create table if not exists building.tenants
(
  id               bigint auto_increment
  primary key,
  user_id          bigint                      null,
  housing_unit_id  bigint                      null,
  move_in_date     date                        not null,
  move_out_date    date                        null,
  security_deposit decimal(10, 2) default 0.00 null,
  constraint user_id
  unique (user_id),
  constraint tenants_ibfk_1
  foreign key (user_id) references building.users (id),
  constraint tenants_ibfk_2
  foreign key (housing_unit_id) references building.housting_units (id)
  );

create table if not exists building.invoices
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
  constraint invoices_ibfk_1
  foreign key (tenant_id) references building.tenants (id),
  check (`month` between 1 and 12)
  );

create index tenant_id
  on building.invoices (tenant_id);

create table if not exists building.issues
(
  id               bigint auto_increment
  primary key,
  tenant_id        bigint                   null,
  title            varchar(150)             not null,
  description      tinytext                 null,
  declaration_date date default (curdate()) null,
  status           tinytext                 not null,
  constraint issues_ibfk_1
  foreign key (tenant_id) references building.tenants (id)
  );

create index tenant_id
  on building.issues (tenant_id);

create table if not exists building.rents
(
  id           bigint auto_increment
  primary key,
  tenant_id    bigint         null,
  month        int            null,
  year         int            null,
  amount       decimal(10, 2) not null,
  payment_date date           null,
  status       tinytext       not null,
  constraint rents_ibfk_1
  foreign key (tenant_id) references building.tenants (id),
  check (`month` between 1 and 12)
  );

create index idx_tenant_id
  on building.rents (tenant_id);

create index idx_housing_unit_id
  on building.tenants (housing_unit_id);

create index idx_user_id
  on building.tenants (user_id);

