-- Tabla Account
CREATE TABLE if NOT EXISTS accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    number VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    initial_amount NUMERIC(15,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    client_id BIGINT NOT NULL
);

-- Tabla Transaction
CREATE TABLE if NOT EXISTS transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    balance NUMERIC(15,2) NOT NULL,
    account_id BIGINT NOT NULL,

    CONSTRAINT fk_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
        ON DELETE CASCADE
);