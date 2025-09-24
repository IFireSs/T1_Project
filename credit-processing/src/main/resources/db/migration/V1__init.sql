CREATE TABLE IF NOT EXISTS product_registry (
    id             BIGSERIAL     PRIMARY KEY,
    client_id      BIGINT        NOT NULL,
    account_id     BIGINT        NOT NULL,
    product_id     BIGINT        NOT NULL,
    interest_rate  NUMERIC(9,6)  NOT NULL,
    open_date      DATE          NOT NULL
    );
CREATE INDEX IF NOT EXISTS ix_pr_client_id  ON product_registry(client_id);
CREATE INDEX IF NOT EXISTS ix_pr_account_id ON product_registry(account_id);
CREATE INDEX IF NOT EXISTS ix_pr_product_id ON product_registry(product_id);
CREATE INDEX IF NOT EXISTS ix_pr_open_date  ON product_registry(open_date);

CREATE TABLE IF NOT EXISTS payment_registry (
    id                    BIGSERIAL     PRIMARY KEY,
    product_registry_id   BIGINT        NOT NULL,
    payment_date          DATE          NOT NULL,
    amount                NUMERIC(18,2) NOT NULL,
    interest_rate_amount  NUMERIC(18,2) NOT NULL,
    debt_amount           NUMERIC(18,2) NOT NULL,
    expired               BOOLEAN       NOT NULL DEFAULT FALSE,
    payment_expiration_date DATE
    );

CREATE INDEX IF NOT EXISTS ix_payreg_pr_id         ON payment_registry(product_registry_id);
CREATE INDEX IF NOT EXISTS ix_payreg_payment_date  ON payment_registry(payment_date);
CREATE INDEX IF NOT EXISTS ix_payreg_expired       ON payment_registry(expired);

ALTER TABLE payment_registry
    ADD CONSTRAINT fk_payment_registry_pr
        FOREIGN KEY (product_registry_id) REFERENCES product_registry(id)
            ON DELETE CASCADE;