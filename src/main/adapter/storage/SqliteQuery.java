package main.adapter.storage;

public enum SqliteQuery {
    SELECT_ALL_ACCOUNTS("SELECT key, timestamp, name, balance FROM account_balances;"),

    SELECT_ALL_TRANSACTIONS("SELECT accountKey, description, amount, timestamp FROM active_transactions;"),

    INSERT_NEW_ACCOUNT("INSERT INTO accounts (key, name, timestamp) VALUES (?, ?, REPLACE(DATETIME(?), ' ', 'T') || '-06:00');"),

    INSERT_NEW_TRANSACTION("INSERT INTO transactions (accountKey, timestamp, amount, description) " +
            "VALUES (?, REPLACE(DATETIME(?), ' ', 'T') || '-06:00', ?, ?);"),

    CLOSE_ACCOUNT("INSERT INTO account_closures (key, timestamp) VALUES (?, REPLACE(DATETIME(?), ' ', 'T') || '-06:00');"),

    INSERT_NEW_ROUND("INSERT INTO rounds (key, timestamp) VALUES (?, REPLACE(DATETIME(?), ' ', 'T') || '-06:00');"),

    INSERT_NEW_DECK("INSERT INTO decks (roundKey, deckKey) VALUES (?, ?);"),

    INSERT_DEALER_CARD("INSERT INTO dealer_cards (timestamp, cardKey) VALUES (REPLACE(DATETIME(?), ' ', 'T') || '-06:00', ?)"),

    INSERT_NEW_ACTION("INSERT INTO action_instances(timestamp, roundKey, accountKey, actionName) " +
            "VALUES (REPLACE(DATETIME(?), ' ', 'T') || '-06:00', ?, ?, ?)"),

    CONNECTION_URL("jdbc:sqlite:./db/blackjack.db");

    private final String sql;

    SqliteQuery(String sql) {
       this.sql = sql;
    }

    public String query() {
        return sql;
    }
}
