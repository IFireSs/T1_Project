\connect clientdb;

CREATE SCHEMA IF NOT EXISTS client AUTHORIZATION client;
ALTER ROLE client SET search_path TO client, public;
GRANT ALL PRIVILEGES ON SCHEMA client TO client;
