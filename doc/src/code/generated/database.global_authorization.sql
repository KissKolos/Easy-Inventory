create table if not exists global_authorization (
    user int not null,
    authorization enum (
        "view_warehouses",
        "delete_warehouses",
        "create_warehouses",
        "modify_warehouses",
        "delete_types",
        "create_types",
        "modify_types",
        "view_users",
        "delete_users",
        "create_users",
        "modify_users"),

    primary key(user,authorization),
    foreign key(user) references users(id)
)$
