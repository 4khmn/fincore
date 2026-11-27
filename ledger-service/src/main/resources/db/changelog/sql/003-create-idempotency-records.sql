--liquibase formatted sql

--changeset denis_akhmin:003-create-idempotency-records

CREATE TABLE idempotency_records (
        id UUID PRIMARY KEY,
        user_id UUID NOT NULL,
        idempotency_key UUID NOT NULL,
        request_hash VARCHAR(64) NOT NULL,
        transaction_id UUID NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        CONSTRAINT uq_idempotency_user_key
        UNIQUE (user_id, idempotency_key),

        CONSTRAINT fk_idempotency_transaction
            FOREIGN KEY (transaction_id)
                REFERENCES ledger_transactions (id)
);

CREATE INDEX idx_idempotency_transaction_id
    ON idempotency_records (transaction_id);