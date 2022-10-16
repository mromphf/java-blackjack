CREATE TABLE IF NOT EXISTS cards
(
    deckKey     TEXT        NOT NULL,
    cardKey TEXT UNIQUE NOT NULL,
    ordinal INT         NOT NULL,
    suit    TEXT        NOT NULL
        REFERENCES suits (name),
    rank    INT         NOT NULL
        REFERENCES ranks (value),
    UNIQUE (deckKey, ordinal),
    UNIQUE (deckKey, cardKey)
);

CREATE TABLE IF NOT EXISTS card_instances
(
    roundKey   TEXT NOT NULL
        REFERENCES rounds (key),
    cardKey    TEXT NOT NULL
        REFERENCES cards (cardKey),
    accountKey TEXT NOT NULL
        REFERENCES accounts (key)
);
