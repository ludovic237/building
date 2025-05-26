create table service_usage
(
    id              bigint auto_increment
        primary key,
    subscription_id bigint                              not null,
    option_id       bigint                              not null,
    quantity_used   int                                 not null,
    usage_date      timestamp default CURRENT_TIMESTAMP null,
    created_date    datetime                            null,
    updated_date    datetime                            null,
    constraint service_usage_ibfk_1
        foreign key (subscription_id) references subscriptions (id)
            on delete cascade,
    constraint service_usage_ibfk_2
        foreign key (option_id) references subscription_options (option_id)
            on delete cascade
);

create index option_id
    on service_usage (option_id);

create index subscription_id
    on service_usage (subscription_id);

