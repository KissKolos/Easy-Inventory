create table if not exists users (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    name varchar(255),
    manager int,
    passhash varchar(255) not null,

    primary key(id),
    foreign key(manager) references users(id)
)$
