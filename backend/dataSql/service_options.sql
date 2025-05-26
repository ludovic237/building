create table service_options
(
    id           bigint auto_increment
        primary key,
    service_id   bigint               not null,
    name         varchar(255)         not null,
    price        decimal(10, 2)       not null,
    max_quantity int        default 0 null,
    is_active    tinyint(1) default 1 null,
    created_date datetime             null,
    updated_date datetime             null,
    quantity     int                  null,
    constraint service_options_ibfk_1
        foreign key (service_id) references services (id)
            on delete cascade
);

create index service_id
    on service_options (service_id);

