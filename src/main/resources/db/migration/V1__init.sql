CREATE TABLE IF NOT EXISTS account (
                                       id BIGSERIAL PRIMARY KEY,
                                       client_id BIGINT NOT NULL,
                                       product_id BIGINT NOT NULL,
                                       balance NUMERIC(18,2) NOT NULL DEFAULT 0,
    interest_rate NUMERIC(6,3) NOT NULL DEFAULT 0,
    is_recalc BOOLEAN NOT NULL DEFAULT FALSE,
    card_exist BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL
    );