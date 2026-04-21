\connect creditdb;

CREATE SCHEMA IF NOT EXISTS credit AUTHORIZATION credit;
ALTER ROLE credit SET search_path TO credit, public;
GRANT ALL PRIVILEGES ON SCHEMA credit TO credit;
