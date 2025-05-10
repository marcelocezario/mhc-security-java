create schema auth;

create table auth.users (
    id uuid not null default random_uuid(),
    username varchar(255) not null unique,
    password varchar(255) not null,
    active boolean not null,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    primary key (id)
);

create table auth.users_roles (
    user_id uuid not null,
    role integer not null
);

create table auth.revoked_tokens (
    token_uuid uuid not null primary key,
    user_id uuid not null,
    revoked_at timestamp(6) with time zone
);