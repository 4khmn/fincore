--liquibase formatted sql

--changeset denis_akhmin:001-init

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    owner_user_id UUID,
    account_type VARCHAR(64) NOT NULL,
    currency VARCHAR(6) NOT NULL, -- RUB, EUR, USD
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, BLOCKED, CLOSED,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account_balances (
    account_id UUID PRIMARY KEY,
    balance_minor BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_balances_account
        FOREIGN KEY (account_id)
            REFERENCES accounts (id)
);

CREATE TABLE ledger_transactions (
    id UUID PRIMARY KEY,
    operation_id UUID UNIQUE NOT NULL,
    operation_type VARCHAR(30) NOT NULL, -- TOP_UP, TRANSFER
    status VARCHAR(20) NOT NULL, -- PENDING, COMPLETED, FAILED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ledger_entries(
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    amount_minor BIGINT NOT NULL,
    currency VARCHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ledger_entries_account
        FOREIGN KEY (account_id)
            REFERENCES accounts (id),
    CONSTRAINT fk_ledger_entries_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES ledger_transactions (id)
);


CREATE INDEX idx_ledger_entries_account_id ON ledger_entries (account_id);