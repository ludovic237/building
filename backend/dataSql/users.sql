create table users
(
    id                bigint auto_increment
        primary key,
    first_name        varchar(100)         not null,
    last_name         varchar(100)         not null,
    email             varchar(150)         not null,
    password          varchar(255)         not null,
    phone             varchar(20)          null,
    role              tinytext             not null,
    birthday          datetime             null,
    gender            tinytext             null,
    image             varchar(255)         null,
    is_active         tinyint(1) default 1 not null,
    is_deleted        tinyint(1) default 0 not null,
    registration_date datetime             not null,
    joined_date       datetime             null,
    created_date      datetime             null,
    updated_date      datetime             null,
    username          varchar(255)         not null,
    constraint email
        unique (email),
    constraint username
        unique (username)
);

INSERT INTO building.users (id, first_name, last_name, email, password, phone, role, birthday, gender, image, is_active, is_deleted, registration_date, joined_date, created_date, updated_date, username) VALUES (1, 'admin', 'admin', 'admin@gmail.com', '$2a$10$mFO/5bu5du/ys9muLzTxTuROjZWPhA2PQXWgMrPdIDRc0e5FsToNy', '11111111', 'ADMIN', null, null, null, 0, 0, '2025-05-25 17:55:04', null, null, null, 'admin@gmail.com');
