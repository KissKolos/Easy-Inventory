create or replace view authentication_token_view as
    select token,external_id user,expiration from authentication_token
        inner join users on users.id=authentication_token.user$
