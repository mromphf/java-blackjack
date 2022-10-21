CREATE TABLE IF NOT EXISTS accounts
(
    key       TEXT UNIQUE PRIMARY KEY NOT NULL CHECK (
        LENGTH(key) == 36
        ),
    timestamp TEXT UNIQUE             NOT NULL,
    name      TEXT                    NOT NULL
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
    timestamp TEXT NOT NULL,
    is_closed INT  NOT NULL DEFAULT 1 CHECK (is_closed == 0 OR is_closed == 1)
);

CREATE TABLE IF NOT EXISTS rounds
(
    key       TEXT PRIMARY KEY UNIQUE NOT NULL CHECK (
        LENGTH(key) == 36
        ),
    timestamp TEXT UNIQUE             NOT NULL
);

CREATE TABLE IF NOT EXISTS decks
(
    roundKey TEXT        NOT NULL
        REFERENCES rounds (key),
    deckKey  TEXT UNIQUE NOT NULL,
    UNIQUE(roundKey, deckKey)
);

CREATE TABLE IF NOT EXISTS suits
(
    name TEXT PRIMARY KEY UNIQUE NOT NULL CHECK (
            name IN ('Hearts',
                     'Clubs',
                     'Diamonds',
                     'Spades'))
);

INSERT INTO suits
VALUES ('Clubs'),
       ('Diamonds'),
       ('Hearts'),
       ('Spades');

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

INSERT INTO ranks
VALUES (1, 'Ace', 1),
       (2, 'Two', 2),
       (3, 'Three', 3),
       (4, 'Four', 4),
       (5, 'Five', 5),
       (6, 'Six', 6),
       (7, 'Seven', 7),
       (8, 'Eight', 8),
       (9, 'Nine', 9),
       (10, 'Ten', 10),
       (11, 'Jack', 10),
       (12, 'Queen', 10),
       (13, 'King', 10);

CREATE TABLE IF NOT EXISTS cards
(
    deckKey TEXT        NOT NULL
        REFERENCES decks (deckKey),
    cardKey TEXT UNIQUE NOT NULL,
    ordinal INT         NOT NULL,
    suit    TEXT        NOT NULL
        REFERENCES suits (name),
    rank    INT         NOT NULL
        REFERENCES ranks (value),
    UNIQUE (deckKey, ordinal),
    UNIQUE (deckKey, cardKey)
);

CREATE TABLE IF NOT EXISTS player_cards
(
    handKey    TEXT NOT NULL,
    cardKey    TEXT NOT NULL
        REFERENCES cards (cardKey),
    accountKey TEXT NOT NULL
        REFERENCES accounts (key),
    UNIQUE (handKey, cardKey)
);

CREATE TABLE IF NOT EXISTS actions
(
    name     TEXT PRIMARY KEY NOT NULL CHECK (
            name IN ('Bet', 'Buy_Insurance', 'Double',
                     'Hit', 'Next', 'Refill',
                     'Settle', 'Split', 'Stand', 'Waive_Insurance'
            )),
    endsTurn INT              NOT NULL CHECK (endsTurn == 0 OR endsTurn == 1)
);

INSERT INTO actions
VALUES ('Bet', 0),
       ('Buy_Insurance', 0),
       ('Double', 1),
       ('Hit', 0),
       ('Next', 0),
       ('Refill', 0),
       ('Settle', 0),
       ('Split', 0),
       ('Stand', 1),
       ('Waive_Insurance', 0);

CREATE TABLE IF NOT EXISTS action_instances
(
    timestamp  TEXT NOT NULL,
    roundKey   TEXT NOT NULL
        REFERENCES rounds (key),
    accountKey TEXT NOT NULL
        REFERENCES accounts (key)
        CHECK (accountKey != roundKey),
    actionName TEXT NOT NULL
        REFERENCES actions (name)
);
