create or replace view warehouses_view as
    select external_id id,name,latitude,longitude,address,deleted from warehouses$
