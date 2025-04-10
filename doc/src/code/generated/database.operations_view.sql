create or replace view operations_view as
    select operations.external_id id,warehouses.external_id warehouse,warehouses.name warehouse_name,warehouses.address warehouse_address,warehouses.deleted warehouse_deleted,operations.name,type,created,commited from operations
        inner join warehouses on operations.warehouse=warehouses.id$
