CREATE TABLE IF NOT EXISTS black_list (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255),
    document_id VARCHAR(64),
    phone       VARCHAR(32),
    reason      VARCHAR(512),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS ix_blacklist_email       ON black_list(email);
CREATE INDEX IF NOT EXISTS ix_blacklist_document_id ON black_list(document_id);
CREATE INDEX IF NOT EXISTS ix_blacklist_phone       ON black_list(phone);