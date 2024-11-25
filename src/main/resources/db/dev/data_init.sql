-- Clear existing data to avoid duplicates and ensure consistent state
TRUNCATE TABLE card_transaction RESTART IDENTITY CASCADE;
TRUNCATE TABLE account_balance RESTART IDENTITY CASCADE;
TRUNCATE TABLE account RESTART IDENTITY CASCADE;

-- ############################
-- Step 1: Insert Accounts
-- ############################
INSERT INTO account (name) VALUES
    ('John Doe'),    -- Account 1
    ('Jane Smith'),  -- Account 2
    ('Carlos Silva'); -- Account 3

-- ############################
-- Step 2: Insert Account Balances
-- ############################

-- Account Balances for Account 1 (John Doe)
INSERT INTO account_balance (account_id, account_balance_type, amount) VALUES
    (1, 'FOOD', 150.00),
    (1, 'MEAL', 200.00),
    (1, 'CASH', 500.00);

-- Account Balances for Account 2 (Jane Smith)
INSERT INTO account_balance (account_id, account_balance_type, amount) VALUES
    (2, 'FOOD', 150.00),
    (2, 'MEAL', 200.00),
    (2, 'CASH', 500.00);

-- Account Balances for Account 3 (Carlos Silva)
INSERT INTO account_balance (account_id, account_balance_type, amount) VALUES
    (3, 'FOOD', 150.00),
    (3, 'MEAL', 200.00),
    (3, 'CASH', 500.00);

-- ############################
-- Step 3: Insert Card Transactions
-- ############################

-- Temporary table for random MCC codes
CREATE TEMP TABLE temp_mcc (mcc_code VARCHAR(4));
INSERT INTO temp_mcc (mcc_code) VALUES
    ('5411'), -- Grocery Stores
    ('5412'), -- Convenience Stores
    ('5811'), -- Restaurants
    ('5812'), -- Fast Food
    ('5999'), -- Miscellaneous
    ('1234'), -- Custom Merchant Code
    ('5678'); -- Custom Merchant Code

-- Generate transactions for each account
DO $$
DECLARE
    account_id BIGINT;
    transaction_count INT;
    mcc_code TEXT;
    status TEXT;
    merchant TEXT;
    amount DECIMAL;
BEGIN
    -- Loop through each account (1 to 3)
    FOR account_id IN 1..3 LOOP
        -- Generate 20 transactions per account
        FOR transaction_count IN 1..20 LOOP
            -- Randomly select MCC code (table column explicitly referenced as "temp_mcc.mcc_code")
            SELECT temp_mcc.mcc_code FROM temp_mcc ORDER BY random() LIMIT 1 INTO mcc_code;

            -- Random transaction amount between 50.00 and 300.00
            amount := random() * (300.00 - 50.00) + 50.00;

            -- Randomly decide transaction status (APPROVED or DENIED)
            status := CASE WHEN random() > 0.5 THEN 'APPROVED' ELSE 'DENIED' END;

            -- Generate merchant name dynamically (e.g., Merchant 1, Merchant 2)
            merchant := 'Merchant ' || trunc(random() * 10 + 1);

            -- Insert the transaction
            INSERT INTO card_transaction (
                account, mcc, total_amount, card_transaction_status, merchant
            ) VALUES (
                account_id::VARCHAR, -- Account ID as string
                mcc_code,            -- MCC Code
                amount,              -- Transaction Amount
                status,              -- Transaction Status
                merchant             -- Merchant Name
            );
        END LOOP;
    END LOOP;
END $$;

-- ############################
-- Step 4: Verify Data (Optional)
-- ############################

-- Count records in each table
SELECT 'Accounts Count:' AS label, COUNT(*) AS total FROM account;
SELECT 'Account Balances Count:' AS label, COUNT(*) AS total FROM account_balance;
SELECT 'Card Transactions Count:' AS label, COUNT(*) AS total FROM card_transaction;
