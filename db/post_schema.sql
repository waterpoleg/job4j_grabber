create table if not exists post (
    id serial primary key,
    title varchar (255),
    description text,
    link text unique,
    created timestamp
);
