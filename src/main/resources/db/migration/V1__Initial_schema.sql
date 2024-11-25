CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE account_balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    account_balance_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE card_transaction (
    id BIGSERIAL PRIMARY KEY,
    account VARCHAR(255) NOT NULL,
    mcc VARCHAR(4) NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    card_transaction_status VARCHAR(50) NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
