create table subscription_options
(
    id                      bigint auto_increment
        primary key,
    subscription_service_id bigint                      not null,
    subscription_id         bigint                      null,
    option_id               bigint                      not null,
    quantity                int            default 1    null,
    price                   decimal(38, 2)              null,
    created_date            datetime                    null,
    updated_date            datetime                    null,
    amount_due              decimal(10, 2) default 0.00 not null,
    constraint subscription_options_ibfk_1
        foreign key (subscription_service_id) references subscription_services (id),
    constraint subscription_options_ibfk_2
        foreign key (option_id) references service_options (id),
    constraint subscription_options_ibfk_4
        foreign key (subscription_id) references subscriptions (id)
);

create index idx_subscription_service_id
    on subscription_options (subscription_service_id);

create index option_id
    on subscription_options (option_id);

create index subscription_id
    on subscription_options (subscription_id);

