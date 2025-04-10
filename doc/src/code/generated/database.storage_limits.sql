create table if not exists storage_limits (
    storage int not null,
    item_type int not null,
    amount int not null,

    primary key(storage,item_type),
    foreign key(storage) references storages(id),
    foreign key(item_type) references item_types(id)
)$
