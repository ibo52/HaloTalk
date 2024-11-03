create table User(
    id integer not NULL primary key,
    name TEXT default not NULL,
    surname TEXT default '*',
    status INT not NULL default 0,
    statusMessage TEXT not NULL default 'Merhaba, Hola, Konnichiwa, Shalom, Hello, Ni hao.'
    UUID TEXT not NULL
);