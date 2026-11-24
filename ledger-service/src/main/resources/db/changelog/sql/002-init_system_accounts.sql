--liquibase formatted sql

--changeset denis_akhmin:002-init_system_accounts

WITH inserted_accounts AS (
    INSERT INTO accounts (id, account_type, currency)
        VALUES
            (gen_random_uuid(), 'SYSTEM', 'RUB'),
            (gen_random_uuid(), 'SYSTEM', 'EUR'),
            (gen_random_uuid(), 'SYSTEM', 'USD')
        RETURNING id
)
INSERT INTO account_balances (account_id, balance_minor)
SELECT id, 0
FROM inserted_accounts;