-- Check if the role exists before creating it
DO
$$
BEGIN
    -- If the role doesn't exist, create it
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'demo_dev_rw') THEN
        CREATE ROLE demo_dev_rw WITH LOGIN PASSWORD 'dev_database_passwd';
    END IF;
END
$$;

-- Grant privileges to the role
GRANT ALL PRIVILEGES ON DATABASE demo_db TO demo_dev_rw;
