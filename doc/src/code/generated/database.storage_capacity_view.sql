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
