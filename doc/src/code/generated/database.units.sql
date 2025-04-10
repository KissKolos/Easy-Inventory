create table if not exists units (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    name varchar(255) not null,
    deleted int default null,

    primary key(id)
)$
