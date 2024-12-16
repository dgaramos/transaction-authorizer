CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account_balance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    account_balance_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT unique_account_balance_type UNIQUE (account_id, account_balance_type)
);

CREATE TABLE card_transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    account_balance_id UUID NOT NULL,
    account VARCHAR(255) NOT NULL,
    mcc VARCHAR(4) NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    card_transaction_status VARCHAR(50) NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT fk_account_balance FOREIGN KEY (account_balance_id) REFERENCES account_balance(id)
);
