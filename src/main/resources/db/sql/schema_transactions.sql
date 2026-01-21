CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    account_number VARCHAR(34) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,          -- example: (CHECKING, SAVINGS, CREDIT)
    currency CHAR(3) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    CONSTRAINT chk_account_type CHECK (account_type IN ('CHECKING','SAVINGS','CREDIT','WALLET')),
    CONSTRAINT chk_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    operation_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    value_date TIMESTAMP WITH TIME ZONE,
    amount NUMERIC(18,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    direction CHAR(1) NOT NULL,                 -- (C = credit, D = debit)
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL,                -- (PENDING, POSTED, REVERSED)
    description VARCHAR(255),
    reference VARCHAR(100),
    merchant VARCHAR(120),
    counterparty_account VARCHAR(34),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT chk_tx_direction CHECK (direction IN ('C','D')),
    CONSTRAINT chk_tx_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_tx_status CHECK (status IN ('PENDING','POSTED','REVERSED')),
    CONSTRAINT chk_tx_amount CHECK (amount <> 0)
);

CREATE INDEX IF NOT EXISTS idx_accounts_user ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_active ON accounts(is_active);
CREATE INDEX IF NOT EXISTS idx_tx_account ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_tx_operation_date ON transactions(operation_date);
CREATE INDEX IF NOT EXISTS idx_tx_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_tx_direction ON transactions(direction);
CREATE INDEX IF NOT EXISTS idx_tx_currency ON transactions(currency);
