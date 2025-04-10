create or replace view users_view as
    select a.external_id id,a.name `name`,b.external_id manager,b.name manager_name,a.passhash passhash from users a
        left join users b on a.manager=b.id$
