--liquibase formatted sql

--changeset Grigoryev_Pavel:1
CREATE TABLE IF NOT EXISTS banks
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    address      VARCHAR(200) NOT NULL,
    phone_number VARCHAR(40)  NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    lastname      VARCHAR(40) NOT NULL,
    firstname     VARCHAR(40) NOT NULL,
    surname       VARCHAR(40) NOT NULL,
    birthdate     DATE        NOT NULL,
    mobile_number varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts
(
    id           VARCHAR(40) PRIMARY KEY      NOT NULL,
    currency     VARCHAR(20)                  NOT NULL,
    balance      NUMERIC                      NOT NULL,
    opening_date DATE                         NOT NULL,
    closing_date DATE,
    bank_id      BIGINT REFERENCES banks (id) NOT NULL,
    user_id      BIGINT REFERENCES users (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    id                 BIGSERIAL PRIMARY KEY,
    date               DATE         NOT NULL,
    time               TIME         NOT NULL,
    type               VARCHAR(20)  NOT NULL,
    senders_bank       VARCHAR(200),
    recipients_bank    VARCHAR(200) NOT NULL,
    senders_account    VARCHAR(40),
    recipients_account VARCHAR(40)  NOT NULL,
    sum                NUMERIC      NOT NULL
);
