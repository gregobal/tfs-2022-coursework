insert into public.community (id, name, description)
values ('a0e1dfcb-c04c-4e89-ad63-57c64600a763', 'Tinkoff Fintech', 'Best place to learn Scala'),
       ('c7e66f3c-f8a3-45ad-af97-9a70988a2fad', 'Scala RU', 'Russian community of Scala developers'),
       ('ef897016-76aa-491b-94f8-c189d2bb4c6a', 'Better Java', 'Club of lover coffee and Scala'),
       ('c5a881d5-6e01-4917-b075-aa68aa691fb5', 'ZIO',
        'Type-safe, composable asynchronous and concurrent programming for Scala'),
       ('e82b6abe-4fb5-45cd-9e02-d83933447ed1', 'Simple Java', 'A place to be nostalgic');

insert into public.member (id, email, community_id, is_notify)
values ('bfe2076e-7f9d-45bc-981d-7d7415acb630', 'student@mail.com', 'a0e1dfcb-c04c-4e89-ad63-57c64600a763', true),
       ('094f1b29-a4dd-4c98-b527-aa7506a68e19', 'speaker@mail.com', 'a0e1dfcb-c04c-4e89-ad63-57c64600a763', false);

insert into public.event (id, community_id, title, description, datetime, location, link, capacity)
values ('03f6657e-d1e2-4d63-b3b5-d7f234ecf698', 'c7e66f3c-f8a3-45ad-af97-9a70988a2fad', 'Ziverge''s evening',
        'Lets discuss about ZIO', '2022-05-30 20:00:00.000000 +00:00', 'online', 'http://https://ziverge.com/', 30),
       ('a153d01d-0a57-46e5-977d-31389bed3568', 'c7e66f3c-f8a3-45ad-af97-9a70988a2fad', 'Scala talks',
        'Top speeches about FP', '2022-06-27 15:00:00.000000 +00:00', 'Moscow', 'http://scala.ru', 1000),
       ('3bdd1602-3a24-4ca9-b908-18b6ea4131a5', 'a0e1dfcb-c04c-4e89-ad63-57c64600a763', 'ZIO workshop',
        'ZIO best practices', '2022-05-11 20:00:00.000000 +00:00', 'online', 'http://zio.ru', null);

insert into public.participant (member_id, event_id, ticket)
values ('bfe2076e-7f9d-45bc-981d-7d7415acb630', '03f6657e-d1e2-4d63-b3b5-d7f234ecf698',
        '055806ae-3848-4b98-a4fb-6c340b6179bf');

insert into public.review (id, rating, feedback)
values ('055806ae-3848-4b98-a4fb-6c340b6179bf', 5, 'I learned a lot new features about Scala and ZIO!!!');
