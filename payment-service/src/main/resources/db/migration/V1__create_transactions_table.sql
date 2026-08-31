CREATE TABLE transactions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id       UUID NOT NULL,
    idempotency_key   VARCHAR(255) NOT NULL UNIQUE,
    customer_email    VARCHAR(255),
    customer_phone    VARCHAR(20),
    status            VARCHAR(20) NOT NULL,
    amount            BIGINT NOT NULL,
    currency          VARCHAR(3) NOT NULL,
    description       TEXT,
    metadata          TEXT,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

CREATE INDEX idx_transactions_merchant_id ON transactions(merchant_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_idempotency_key ON transactions(idempotency_key);