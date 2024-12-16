-- ############################
-- Database Initialization and Data Seeding Script
-- ############################

-- Clear existing data to avoid duplicates and ensure consistent state
TRUNCATE TABLE card_transaction RESTART IDENTITY CASCADE;
TRUNCATE TABLE account_balance RESTART IDENTITY CASCADE;
TRUNCATE TABLE account RESTART IDENTITY CASCADE;

-- ############################
-- Step 1: Insert Accounts
-- ############################
-- Insert accounts with UUIDs and versioning
INSERT INTO account (id, name, version) VALUES
    (gen_random_uuid(), 'John Doe', 1),
    (gen_random_uuid(), 'Jane Smith', 1),
    (gen_random_uuid(), 'Carlos Silva', 1);

-- ############################
-- Step 2: Insert Account Balances
-- ############################
-- Dynamically fetch account IDs and insert account balances
WITH accounts AS (
    SELECT id AS account_id FROM account
)
INSERT INTO account_balance (id, account_id, account_balance_type, amount, version)
VALUES
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 0), 'FOOD', 150.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 0), 'MEAL', 200.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 0), 'CASH', 500.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 1), 'FOOD', 150.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 1), 'MEAL', 200.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 1), 'CASH', 500.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 2), 'FOOD', 150.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 2), 'MEAL', 200.00, 1),
    (gen_random_uuid(), (SELECT account_id FROM accounts LIMIT 1 OFFSET 2), 'CASH', 500.00, 1);

-- ############################
-- Step 3: Insert Card Transactions
-- ############################

CREATE TEMP TABLE temp_mcc (mcc_code VARCHAR(4));
INSERT INTO temp_mcc (mcc_code) VALUES
    ('5411'), -- Grocery Stores
    ('5412'), -- Convenience Stores
    ('5811'), -- Restaurants
    ('5812'), -- Fast Food
    ('5999'), -- Miscellaneous
    ('1234'), -- Custom Merchant Code
    ('5678'); -- Custom Merchant Code

DO $$
DECLARE
    acc_id UUID;
    balance_id UUID;
    transaction_count INT;
    mcc_code TEXT;
    status TEXT;
    merchant TEXT;
    amount DECIMAL;
BEGIN
    FOR acc_id IN (SELECT id FROM account) LOOP
        FOR transaction_count IN 1..20 LOOP
            -- Randomly select MCC code
            SELECT temp_mcc.mcc_code FROM temp_mcc ORDER BY random() LIMIT 1 INTO mcc_code;

            -- Generate random transaction amount
            amount := random() * (300.00 - 50.00) + 50.00;

            -- Randomly assign transaction status
            status := CASE WHEN random() > 0.5 THEN 'APPROVED' ELSE 'DENIED' END;

            -- Generate random merchant name
            merchant := 'Merchant ' || trunc(random() * 10 + 1);

            -- Randomly select an account balance ID for the account
            SELECT id INTO balance_id
            FROM account_balance
            WHERE account_id = acc_id
            ORDER BY random() LIMIT 1;

            -- Insert card transaction
            INSERT INTO card_transaction (
                id, account_id, account_balance_id, account, mcc, total_amount, card_transaction_status, merchant, version
            ) VALUES (
                gen_random_uuid(),         -- UUID for the transaction
                acc_id,                    -- Account ID
                balance_id,                -- Account Balance ID
                acc_id::VARCHAR,           -- Account ID as string
                mcc_code,                  -- MCC Code
                amount,                    -- Transaction Amount
                status,                    -- Transaction Status
                merchant,                  -- Merchant Name
                1                          -- Version
            );
        END LOOP;
    END LOOP;
END $$;

-- ############################
-- Step 4: Verify Data (Optional)
-- ############################

SELECT 'Accounts Count:' AS label, COUNT(*) AS total FROM account;
SELECT 'Account Balances Count:' AS label, COUNT(*) AS total FROM account_balance;
SELECT 'Card Transactions Count:' AS label, COUNT(*) AS total FROM card_transaction;

SELECT * FROM card_transaction LIMIT 10;