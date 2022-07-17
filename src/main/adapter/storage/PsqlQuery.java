package main.adapter.storage;

public enum PsqlQuery {
    SELECT_ALL_ACCOUNTS("SELECT * FROM blackjack.account_stats;"),

    SELECT_ALL_TRANSACTIONS("SELECT * FROM blackjack.active_transactions;"),

    INSERT_NEW_ACCOUNT("INSERT INTO blackjack.accounts (key, name, timestamp) VALUES ('%s', '%s', '%s');"),

    INSERT_NEW_TRANSACTION("INSERT INTO blackjack.transactions (accountkey, timestamp, amount, description) " +
            "VALUES ('%s', '%s', '%s', '%s');"),

    CLOSE_ACCOUNT("INSERT INTO blackjack.account_closures (key, timestamp) VALUES ('%s', '%s');");

    public final String sql;

    PsqlQuery(String sql) {
       this.sql = sql;
    }
}