
create database if not exists DBNAME collate utf8mb4_general_ci$

use DBNAME$

create table if not exists units (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    name varchar(255) not null,
    deleted int default null,

    primary key(id)
)$

create table if not exists item_types (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    unit int not null,
    name varchar(255) not null,
    deleted int default null,

    primary key(id),
    foreign key(unit) references units(id)
)$

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

create table if not exists storage_limits (
    storage int not null,
    item_type int not null,
    amount int not null,

    primary key(storage,item_type),
    foreign key(storage) references storages(id),
    foreign key(item_type) references item_types(id)
)$

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

create table if not exists operation_items (
    operation int not null,
    storage int not null,
    item_type int not null,
    amount int not null,
    lot varchar(255),
    global_serial_inner int not null default 0,
    global_serial int as (if(global_serial_inner=0,NULL,global_serial_inner)) virtual,
    manufacturer_serial varchar(255),

    primary key(operation,storage,item_type,lot,global_serial_inner),
    foreign key(operation) references operations(id) on delete cascade,
    foreign key(storage) references storages(id),
    foreign key(item_type) references item_types(id)
)$

create table if not exists users (
    id int auto_increment not null,
    external_id varchar(255) not null unique,

    name varchar(255),
    manager int,
    passhash varchar(255) not null,

    primary key(id),
    foreign key(manager) references users(id)
)$

create table if not exists authorization (
    user int not null,
    warehouse int not null,

    authorization enum ('view','create_add_operation','create_remove_operation','handle_operation','configure'),

    primary key(user,warehouse,authorization),
    foreign key(user) references users(id),
    foreign key(warehouse) references warehouses(id)
)$

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
        "modify_users",
        "view_statistics"),

    primary key(user,authorization),
    foreign key(user) references users(id)
)$

create table if not exists authentication_token (
    token varchar(255) not null,

    user int not null,
    expiration int,

    primary key(token),
    foreign key(user) references users(id)
)$

create or replace view users_view as
    select a.external_id id,a.name `name`,b.external_id manager,b.name manager_name,a.passhash passhash from users a
        left join users b on a.manager=b.id$

create or replace view authorization_view as
    select external_id id,authorization,null warehouse from users
        inner join global_authorization on users.id=global_authorization.user 
    union
    select users.external_id id,authorization,warehouses.external_id warehouse from users
        inner join authorization on users.id=authorization.user
        inner join warehouses on warehouses.id=authorization.warehouse$

create or replace view authentication_token_view as
    select token,external_id user,expiration from authentication_token
        inner join users on users.id=authentication_token.user where expiration>UNIX_TIMESTAMP()$

create or replace view warehouses_view as
    select external_id id,name,latitude,longitude,address,deleted from warehouses$

create or replace view storages_view as
    select warehouses.external_id warehouse,warehouses.name warehouse_name,warehouses.address warehouse_address,warehouses.deleted warehouse_deleted,storages.external_id id,storages.name,storages.deleted deleted from storages
        inner join warehouses on storages.warehouse=warehouses.id$

create or replace view item_stacks_view as
    select
        warehouses.external_id warehouse,
        warehouses.name warehouse_name,
        warehouses.address warehouse_address,
        storages.external_id `storage`,
        storages.name storage_name,
        item_types.external_id item,
        item_types.name item_name,
        units.external_id unit,
        units.name unit_name,
        item_stacks.amount amount,
        item_stacks.lot lot,
        item_stacks.global_serial global_serial,
        item_stacks.manufacturer_serial manufacturer_serial,
        (item_stacks.amount-COALESCE(sum(oi.amount),0)) available_amount
    from item_stacks
        inner join storages on storages.id=item_stacks.storage
        inner join warehouses on storages.warehouse=warehouses.id
        inner join item_types on item_types.id=item_stacks.item_type
        inner join units on item_types.unit=units.id
        left join (
            select * from operation_items inner join operations on operation_items.operation=operations.id where operations.type='remove' and commited is null
        ) oi on (
            oi.storage=item_stacks.storage and
            oi.item_type=item_stacks.item_type and
            oi.lot=item_stacks.lot and
            oi.global_serial_inner=item_stacks.global_serial_inner
        )
    group by
        item_stacks.storage,
        item_stacks.item_type,
        item_stacks.lot,
        item_stacks.global_serial_inner$

create or replace view item_types_view as
    select item_types.external_id id,item_types.name,units.external_id unit,units.name unit_name,units.deleted unit_deleted,item_types.deleted deleted from item_types
        inner join units on units.id=item_types.unit$

create or replace view units_view as
    select external_id id,name,deleted from units$

create or replace view operations_view as
    select operations.external_id id,warehouses.external_id warehouse,warehouses.name warehouse_name,warehouses.address warehouse_address,warehouses.deleted warehouse_deleted,operations.name,type,created,commited from operations
        inner join warehouses on operations.warehouse=warehouses.id$

create or replace view operation_items_view as
    select
        operations.external_id operation,
        operations.type `type`,
        operations.commited commited,
        storages.external_id storage,
        storages.name storage_name,
        storages.deleted storage_deleted,
        warehouses.external_id warehouse,
        warehouses.name warehouse_name,
        warehouses.address warehouse_address,
        warehouses.deleted warehouse_deleted,
        item_types.external_id item,
        item_types.name item_name,
        item_types.deleted item_deleted,
        units.external_id unit,
        units.name unit_name,
        units.deleted unit_deleted,
        amount,
        lot,
        global_serial,
        manufacturer_serial
    from operation_items
        inner join operations on operations.id=operation_items.operation
        inner join storages on storages.id=operation_items.storage
        inner join item_types on item_types.id=operation_items.item_type
        inner join units on units.id=item_types.unit
        left join warehouses on warehouses.id=operations.warehouse$

create or replace view storage_limits_view as
    select
        warehouses.external_id warehouse,
        storages.external_id `storage`,
        item_types.external_id item_type,
        amount
    from storages 
        inner join warehouses on warehouses.id=storages.warehouse
        left join storage_limits on storages.id=storage_limits.storage
        inner join item_types on item_types.id=storage_limits.item_type
    where storages.deleted is null$

create or replace view storage_capacity_view as
    select
        warehouses.external_id warehouse,
        warehouses.name warehouse_name,
        storages.external_id `storage`,
        storages.name storage_name,
        item_types.external_id item,
        item_types.name item_name,
        units.external_id unit,
        units.name unit_name,
        COALESCE(sum(item_stacks.amount),0) amount,
        COALESCE(storage_limits.amount,0) `limit`
    from
        (
            storages
            inner join warehouses on warehouses.id=storages.warehouse,
            item_types
            inner join units on units.id=item_types.unit
        )
        left join storage_limits on (
            storage_limits.storage=storages.id and
            storage_limits.item_type=item_types.id
        )
        left join item_stacks on (
            item_stacks.storage=storages.id and
            item_stacks.item_type=item_types.id
        )
    where storages.deleted is null and item_types.deleted is null
    group by warehouse,`storage`,item$

drop PROCEDURE if exists checkUser$
create PROCEDURE checkUser 
(
   newid int,
   newmanager int
) 
BEGIN 
    declare found int;
    with recursive users_tree as (
        select id from users where id=newid
        union all
        select u.id from users u inner join users_tree on u.manager=users_tree.id
    ) select count(id) into found from users_tree where id=newmanager;
    if found=1 then
        signal sqlstate '45000' set message_text = 'UserError: Trying to create a loop';
    end if;
END
$

drop trigger if exists user_insert_trigger$

create trigger user_insert_trigger before insert on users
for each row
begin
    call checkUser(new.id,new.manager);
end
$

drop trigger if exists user_update_trigger$

create trigger user_update_trigger before update on users
for each row
begin
    call checkUser(new.id,new.manager);
end
$


