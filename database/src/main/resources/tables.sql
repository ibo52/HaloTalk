--creates database tables to log messaging between local and remote
create table Sender
(
    id integer not null primary key,
    name text not null default '',
    surname text not null default '',
    status int not null default 0,
    statusMessage text not null default 'Merhaba, Hola, Konnichiwa, Shalom, Hello, Ni hao.',
    ip text not null
);

--message queue between current device and remote
create table MessageQueue
(   id integer not null primary key,
    senderId text not null default 0,
    msgType text not null default 0,--type of the message content, text str, phone call etc.
    message text not null,
    gmtdate date not null default current_date,
    gmttime time not null default current_time,
    completed integer not null default 1, --citates that either message read by cli or sent by server 

    foreign key(senderId) references Sender(id)
);

--localhost information is mandatory as id of table Sender defaults to 0
insert into sender(id, ip) values(0, 'localhost');

