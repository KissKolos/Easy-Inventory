create table if not exists warehouses (
    id int not null auto_increment,
    external_id varchar(255) not null unique,
    name varchar(255) not null,
    latitude float,
    longitude float,
    address varchar(255),
    deleted int default null,

    primary key(id)
)$
