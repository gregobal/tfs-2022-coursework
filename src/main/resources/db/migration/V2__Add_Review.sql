alter table participant
    add ticket uuid not null default gen_random_uuid();

create unique index participant_ticket_uindex
    on participant (ticket);

create table review
(
    id             uuid not null
        constraint review_pk
            primary key,
    rating         int  not null,
    feedback       varchar,
    constraint review_participant_ticket_fk
        foreign key (id) references participant (ticket)
);

comment on table review is 'Обзор на событие';
