CREATE TABLE ranks
(
    value INT CHECK(value > 0 AND value < 14),
    name TEXT NOT NULL,
    blackjackValue INT CHECK(blackjackValue > 1 AND blackjackValue < 10)
);

CREATE TABLE suits
(
    name TEXT NOT NULL
);

CREATE TABLE rounds
(
    key TEXT,
    timestamp   TEXT NOT NULL
);

CREATE TABLE memberships
(
    roundKey TEXT
        references rounds (key),
    accountKey TEXT
        references accounts (key)
);

CREATE TABLE cards
(
    roundKey TEXT
        references rounds (key),
    accountKey TEXT
        references accounts (key),
    rank INT
        references ranks (value),
    suit TEXT
        references suits (name)
);
