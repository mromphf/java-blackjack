CREATE TABLE IF NOT EXISTS accounts
(
    key       TEXT UNIQUE PRIMARY KEY,
    timestamp TEXT UNIQUE NOT NULL,
    name      TEXT        NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    accountKey  TEXT REFERENCES accounts (key),
    timestamp   TEXT NOT NULL,
    description TEXT NOT NULL,
    amount      INTEGER
);

CREATE TABLE IF NOT EXISTS account_closures
(
    key       TEXT PRIMARY KEY REFERENCES accounts (key),
    timestamp TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS suits
(
    name TEXT PRIMARY KEY UNIQUE NOT NULL CHECK (
            name IN ('Hearts',
                     'Clubs',
                     'Diamonds',
                     'Spades'))
);

CREATE TABLE IF NOT EXISTS ranks
(
    value          INT PRIMARY KEY UNIQUE CHECK (value > 0 AND value < 14),
    name           TEXT UNIQUE NOT NULL CHECK (
            NAME IN ('Ace', 'Two', 'Three', 'Four',
                     'Five', 'Six', 'Seven', 'Eight',
                     'Nine', 'Ten', 'Jack', 'Queen', 'King')),
    blackjackValue INT CHECK (
        blackjackValue > 0 AND blackjackValue < 11)
);
