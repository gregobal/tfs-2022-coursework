create table community
(
    id          uuid    not null
        constraint community_pk
            primary key,
    name        varchar not null,
    description varchar
);

comment on table community is 'Сообщество по инстересам для порождения мероприятий';

create table if not exists event
(
    id           uuid                     not null
        constraint event_pk
            primary key,
    community_id uuid                     not null,
    title        varchar                  not null,
    description  varchar,
    datetime     timestamp with time zone not null,
    location     varchar,
    link         varchar,
    capacity     integer,
    constraint event_community_id_fk
        foreign key (community_id) references community (id)
            on update cascade on delete cascade
);

comment on table event is 'Мероприятие, проводится в рамках сообщества с участием его членов';

create table member
(
    id           uuid    not null
        constraint member_pk
            primary key,
    email        varchar not null,
    community_id uuid    not null,

    constraint member_community_id_fk
        foreign key (community_id) references community (id)
            on update cascade on delete cascade
);

comment on table member is 'Член сообщества';

create table participant
(
    id        uuid not null
        constraint participant_pk
            primary key,
    member_id uuid not null,
    event_id  uuid not null,

    constraint member_id_fk
        foreign key (member_id) references member (id)
            on update cascade on delete cascade,
    constraint event_id_fk
        foreign key (event_id) references event (id)
            on update cascade on delete cascade
);

comment on table participant is 'Участник мероприятия';
