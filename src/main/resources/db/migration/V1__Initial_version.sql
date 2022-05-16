create table if not exists event
(
    id          varchar(255)             not null
        constraint event_pk
            primary key,
    title       varchar                  not null,
    description varchar,
    datetime    timestamp with time zone not null,
    location    varchar,
    link        varchar,
    capacity    integer
);

-- alter table event
--     owner to eventus;
