create table if not exists item_types (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    unit int not null,
    name varchar(255) not null,
    deleted int default null,

    primary key(id),
    foreign key(unit) references units(id)
)$
