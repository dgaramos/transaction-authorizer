DO
$$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'demo_db') THEN
        CREATE DATABASE demo_db;
    END IF;
END
$$;