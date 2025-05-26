create table invoice_counter
(
    id      bigint auto_increment
        primary key,
    year    int null,
    counter int null
);

INSERT INTO building.invoice_counter (id, year, counter) VALUES (1, 2025, 4);
