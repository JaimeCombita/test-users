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
