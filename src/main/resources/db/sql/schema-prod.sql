CREATE TABLE IF NOT EXISTS USERS (
    id UUID PRIMARY KEY,
    identification_number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP,
    modified TIMESTAMP,
    is_active BOOLEAN,
    allow_multisession BOOLEAN,
    rol VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS PHONES (
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    city_code VARCHAR(10) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_phone_user FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relname = 'uk_users_email'
          AND n.nspname = 'public'
    ) THEN
        CREATE UNIQUE INDEX uk_users_email ON USERS(email);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_users_name') THEN
        CREATE INDEX idx_users_name ON USERS(name);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_users_is_active') THEN
        CREATE INDEX idx_users_is_active ON USERS(is_active);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_users_rol') THEN
        CREATE INDEX idx_users_rol ON USERS(rol);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_users_created') THEN
        CREATE INDEX idx_users_created ON USERS(created);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_users_modified') THEN
        CREATE INDEX idx_users_modified ON USERS(modified);
    END IF;
END $$;
