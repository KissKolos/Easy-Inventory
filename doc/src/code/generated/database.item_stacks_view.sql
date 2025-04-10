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
