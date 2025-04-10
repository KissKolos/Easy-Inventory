create table if not exists authentication_token (
    token varchar(255) not null,

    user int not null,
    expiration int,

    primary key(token),
    foreign key(user) references users(id)
)$
