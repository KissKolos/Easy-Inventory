create or replace view item_types_view as
    select item_types.external_id id,item_types.name,units.external_id unit,units.name unit_name,units.deleted unit_deleted,item_types.deleted deleted from item_types
        inner join units on units.id=item_types.unit$
