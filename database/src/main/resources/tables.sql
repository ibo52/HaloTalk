--creates database tables to log messaging between local and remote
create table Sender
(
    id integer not null primary key,
    ip text not null unique
);

--message queue between current device and remote
create table MessageQueue
(   id integer not null primary key,
    senderId text not null default 0,
    message text not null,
    gmtdate date not null default current_date,
    gmttime time not null default current_time,
    completed integer not null default 1, --citates that either message read by cli or sent by server 
    --direction integer not null default 0,

    foreign key(senderId) references Sender(id)
);

--localhost information is mandatory as id of table Sender defaults to 0
insert into sender(id, ip) values(0, 'localhost');

