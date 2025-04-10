create table if not exists operations (
    id int auto_increment not null,
    external_id varchar(255) not null,
    warehouse int not null,

    name varchar(255),
    type enum ('add','remove'),
    created int not null,
    commited int,

    primary key(id),
    unique(external_id,warehouse),
    foreign key(warehouse) references warehouses(id)
)$
