-- Buyer beware,
-- This script assumes a database named 'blackjack' exists


CREATE TABLE IF NOT EXISTS blackjack.accounts (
    key UUID PRIMARY KEY,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS blackjack.transactions (
    accountKey UUID PRIMARY KEY REFERENCES blackjack.accounts(key),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    description VARCHAR(20) NOT NULL,
    amount INTEGER
);

CREATE TABLE IF NOT EXISTS blackjack.account_closures (
    key UUID PRIMARY KEY REFERENCES blackjack.accounts(key),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);