--liquibase formatted sql

--changeset liquibase:1
CREATE TABLE address(
   id BIGSERIAL PRIMARY KEY,
    address TEXT UNIQUE
);