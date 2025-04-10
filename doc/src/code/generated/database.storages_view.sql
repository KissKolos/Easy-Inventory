create or replace view storages_view as
    select warehouses.external_id warehouse,warehouses.name warehouse_name,warehouses.address warehouse_address,warehouses.deleted warehouse_deleted,storages.external_id id,storages.name,storages.deleted deleted from storages
        inner join warehouses on storages.warehouse=warehouses.id$
