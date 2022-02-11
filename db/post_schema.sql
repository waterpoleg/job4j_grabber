create table if not exists post (
    id serial primary key,
    title varchar (255),
    description text,
    link varchar (255) unique,
    created timestamp
);
