CREATE UNLOGGED TABLE payments (
    correlationId CHAR(36) PRIMARY KEY,
    amount DECIMAL(15, 2) NOT NULL,
    type SMALLINT NOT NULL,
    createdAt TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_created_at ON payments (createdAt);