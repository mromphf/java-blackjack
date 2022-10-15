CREATE TABLE IF NOT EXISTS decks
(
    key     TEXT        NOT NULL,
    cardKey TEXT UNIQUE NOT NULL,
    ordinal INT         NOT NULL,
    suit    TEXT        NOT NULL
        REFERENCES suits (name),
    rank    INT         NOT NULL
        REFERENCES ranks (value),
    UNIQUE (key, ordinal)
);

CREATE TABLE IF NOT EXISTS card_instances
(
    roundKey   TEXT NOT NULL
        REFERENCES rounds (key),
    cardKey    TEXT NOT NULL
        REFERENCES decks (cardKey),
    accountKey TEXT NOT NULL
        REFERENCES accounts (key)
);
