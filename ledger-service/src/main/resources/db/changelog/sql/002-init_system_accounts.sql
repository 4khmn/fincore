--liquibase formatted sql

--changeset denis_akhmin:002-init_system_accounts

INSERT INTO accounts (account_type, currency) VALUES
    ('SYSTEM', 'RUB'),
    ('SYSTEM', 'EUR'),
    ('SYSTEM', 'USD');

INSERT INTO account_balances (balance_minor) VALUES
    (0),
    (0),
    (0);