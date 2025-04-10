create table if not exists storages (
    id int not null auto_increment,
    warehouse int not null,
    external_id varchar(255) not null,

    name varchar(255) not null,
    deleted int default null,

    foreign key(warehouse) references warehouses(id),
    primary key(id),
    unique (warehouse,external_id)
)$
