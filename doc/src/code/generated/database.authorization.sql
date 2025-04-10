create table if not exists authorization (
    user int not null,
    warehouse int not null,

    authorization enum ('view','create_add_operation','create_remove_operation','handle_operation','configure'),

    primary key(user,warehouse,authorization),
    foreign key(user) references users(id),
    foreign key(warehouse) references warehouses(id)
)$
