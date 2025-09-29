\connect accountdb;

CREATE SCHEMA IF NOT EXISTS account AUTHORIZATION account;
ALTER ROLE account SET search_path TO account, public;
GRANT ALL PRIVILEGES ON SCHEMA account TO account;
