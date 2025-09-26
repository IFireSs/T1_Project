CREATE TABLE IF NOT EXISTS users (
    id     BIGSERIAL PRIMARY KEY,
    login  VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email  VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS clients (
    id               BIGSERIAL PRIMARY KEY,
    client_id        VARCHAR(12)  NOT NULL UNIQUE,
    user_id          BIGINT       NOT NULL,
    first_name       VARCHAR(100),
    middle_name      VARCHAR(100),
    last_name        VARCHAR(100),
    date_of_birth    DATE,
    document_type    VARCHAR(32),
    document_id      VARCHAR(64),
    document_prefix  VARCHAR(32),
    document_suffix  VARCHAR(32),
    CONSTRAINT chk_client_id_digits CHECK (client_id ~ '^[0-9]{12}$')
    );
CREATE INDEX IF NOT EXISTS ix_clients_user_id ON clients(user_id);

CREATE TABLE IF NOT EXISTS products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    "key"       VARCHAR(8)   NOT NULL,
    create_date TIMESTAMPTZ  NOT NULL DEFAULT now(),
    product_id  VARCHAR(64)  GENERATED ALWAYS AS ("key" || id::text) STORED
    );
CREATE UNIQUE INDEX IF NOT EXISTS ux_products_product_id ON products(product_id);
CREATE INDEX IF NOT EXISTS ix_products_key ON products("key");

CREATE TABLE IF NOT EXISTS client_products (
    id         BIGSERIAL PRIMARY KEY,
    client_id  VARCHAR(12)     NOT NULL,
    product_id VARCHAR(64)      NOT NULL,
    open_date  DATE,
    close_date DATE,
    status     VARCHAR(32)
    );
CREATE INDEX IF NOT EXISTS ix_client_products_client_id  ON client_products(client_id);
CREATE INDEX IF NOT EXISTS ix_client_products_product_id ON client_products(product_id);
