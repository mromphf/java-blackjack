package main.adapter.storage;

import static java.lang.System.getenv;

public enum PsqlQuery {
    SELECT_ALL_ACCOUNTS("SELECT * FROM blackjack.account_stats;"),

    SELECT_ALL_TRANSACTIONS("SELECT * FROM blackjack.active_transactions;"),

    INSERT_NEW_ACCOUNT("INSERT INTO blackjack.accounts (key, name, timestamp) VALUES (?, ?, ?);"),

    INSERT_NEW_TRANSACTION("INSERT INTO blackjack.transactions (accountkey, timestamp, amount, description) " +
            "VALUES (?, ?, ?, ?);"),

    CLOSE_ACCOUNT("INSERT INTO blackjack.account_closures (key, timestamp) VALUES (?, ?);"),

    CONNECTION_URL(getenv("PSQL_URL"));

    private final String sql;

    PsqlQuery(String sql) {
       this.sql = sql;
    }

    public String query() {
        return sql;
    }
}
