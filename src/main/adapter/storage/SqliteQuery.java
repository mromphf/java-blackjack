package main.adapter.storage;

public enum SqliteQuery {
    SELECT_ALL_ACCOUNTS("SELECT * FROM account_stats;"),

    SELECT_ALL_TRANSACTIONS("SELECT * FROM active_transactions;"),

    INSERT_NEW_ACCOUNT("INSERT INTO accounts (key, name, timestamp) VALUES ('%s', '%s', '%s');"),

    INSERT_NEW_TRANSACTION("INSERT INTO transactions (accountkey, timestamp, amount, description) " +
            "VALUES ('%s', '%s', '%s', '%s');"),

    CLOSE_ACCOUNT("INSERT INTO account_closures (key, timestamp) VALUES ('%s', '%s');");

    public final String sql;

    SqliteQuery(String sql) {
       this.sql = sql;
    }
}
