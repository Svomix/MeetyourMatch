CREATE TABLE IF NOT EXISTS account_verification_token
(
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER    NOT NULL,
    token      VARCHAR(4) NOT NULL,
    created_at DATE       NOT NULL,
    expired_at DATE       NOT NULL
);

alter table users
    add is_enabled BOOLEAN default false not null;