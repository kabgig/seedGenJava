--liquibase formatted sql

--changeset liquibase:1
CREATE TABLE addresses (
    address TEXT PRIMARY KEY
);