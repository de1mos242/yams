CREATE TABLE users
(
    id       BIGSERIAL NOT NULL PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE
);