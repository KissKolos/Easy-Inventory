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
