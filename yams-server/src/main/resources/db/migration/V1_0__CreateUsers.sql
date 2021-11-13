CREATE TABLE users
(
    id       BIGINT NOT NULL PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE
);

CREATE SEQUENCE user_id_seq
    START 1;