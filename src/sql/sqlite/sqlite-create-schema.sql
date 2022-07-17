-- Buyer beware,
-- This script assumes a database named 'blackjack' exists


CREATE TABLE IF NOT EXISTS accounts (
    key BLOB UNIQUE PRIMARY KEY,
    timestamp TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    accountKey BLOB REFERENCES accounts(key),
    timestamp TEXT NOT NULL,
    description TEXT NOT NULL,
    amount INTEGER
);

CREATE TABLE IF NOT EXISTS account_closures (
    key BLOB PRIMARY KEY REFERENCES accounts(key),
    timestamp TEXT NOT NULL
);