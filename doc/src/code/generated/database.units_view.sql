create or replace view units_view as
    select external_id id,name,deleted from units$
