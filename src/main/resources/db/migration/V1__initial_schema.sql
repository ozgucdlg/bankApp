-- Create account table if it doesn't exist
CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    account_holder_name VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL
);

-- Create auth table if it doesn't exist
CREATE TABLE IF NOT EXISTS auth (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    account_id INTEGER REFERENCES account(id)
);

-- Create notifications table if it doesn't exist
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    notification_type VARCHAR(50),
    sent_at TIMESTAMP,
    status VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE NOT NULL
);

-- Create transactions table if it doesn't exist
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    from_account_id INTEGER REFERENCES account(id),
    to_account_id INTEGER REFERENCES account(id),
    amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255),
    timestamp TIMESTAMP NOT NULL
); 