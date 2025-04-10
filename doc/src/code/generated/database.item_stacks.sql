create table if not exists item_stacks (
    storage int not null,
    item_type int not null,

    amount int not null,
    lot varchar(255),
    global_serial_inner int not null default 0,
    global_serial int as (if(global_serial_inner=0,NULL,global_serial_inner)) virtual,
    manufacturer_serial varchar(255),

    unique(global_serial),
    foreign key(storage) references storages(id),
    foreign key(item_type) references item_types(id),
    primary key(storage,item_type,lot,global_serial_inner)
)$
