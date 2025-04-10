create or replace view authorization_view as
    select external_id id,authorization,null warehouse from users
        inner join global_authorization on users.id=global_authorization.user 
    union
    select users.external_id id,authorization,warehouses.external_id warehouse from users
        inner join authorization on users.id=authorization.user
        inner join warehouses on warehouses.id=authorization.warehouse$
