CREATE TABLE IF NOT EXISTS accounts (
    id            BIGSERIAL PRIMARY KEY,
    client_id     BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    balance       NUMERIC(18,2) NOT NULL DEFAULT 0,
    interest_rate NUMERIC(6,3)  NOT NULL DEFAULT 0,
    is_recalc     BOOLEAN       NOT NULL DEFAULT FALSE,
    card_exist    BOOLEAN       NOT NULL DEFAULT FALSE,
    status        VARCHAR(20)   NOT NULL,
    CONSTRAINT chk_account_status CHECK (status IN ('ACTIVE','CLOSED','BLOCKED','ARRESTED'))
    );
CREATE INDEX IF NOT EXISTS ix_account_client_id ON accounts(client_id);
CREATE INDEX IF NOT EXISTS ix_accounts_product_id ON accounts(product_id);

CREATE TABLE IF NOT EXISTS cards (
    id             BIGSERIAL PRIMARY KEY,
    account_id     BIGINT NOT NULL REFERENCES accounts(id),
    card_id        VARCHAR(32) NOT NULL UNIQUE,
    payment_system VARCHAR(20) NOT NULL,
    status         VARCHAR(20) NOT NULL
    );
CREATE INDEX IF NOT EXISTS ix_cards_account_id ON cards(account_id);

ALTER TABLE cards
    ADD CONSTRAINT fk_cards_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS payments (
    id           BIGSERIAL PRIMARY KEY,
    account_id   BIGINT NOT NULL REFERENCES accounts(id),
    payment_date DATE   NOT NULL,
    amount       NUMERIC(18,2) NOT NULL,
    is_credit    BOOLEAN NOT NULL,
    payed_at     TIMESTAMPTZ,
    type         VARCHAR(50) NOT NULL
    );
CREATE INDEX IF NOT EXISTS ix_payments_account_id   ON payments(account_id);
CREATE INDEX IF NOT EXISTS ix_payments_payment_date ON payments(payment_date);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS transactions (
    id         BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    card_id    BIGINT REFERENCES cards(id),
    type       VARCHAR(50) NOT NULL,
    amount     NUMERIC(18,2) NOT NULL,
    status     VARCHAR(20)   NOT NULL,
    timestamp         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT chk_tx_status CHECK (status IN ('ALLOWED','PROCESSING','COMPLETE','BLOCKED','CANCELLED'))
    );
CREATE INDEX IF NOT EXISTS ix_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS ix_transactions_card_id    ON transactions(card_id);
CREATE INDEX IF NOT EXISTS ix_transactions_timestamp  ON transactions(timestamp);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_card
        FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE SET NULL;