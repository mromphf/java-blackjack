package main.adapter.storage;

public enum SqliteQuery {
    SELECT_ALL_ACCOUNTS("SELECT key, REPLACE(DATETIME(timestamp), ' ', 'T') || '-06:00' as timestamp, name, balance " +
            " FROM account_balances;"),

    SELECT_ALL_TRANSACTIONS("SELECT accountKey, description, amount, REPLACE(DATETIME(timestamp), ' ', 'T') || '-06:00' as timestamp " +
            " FROM active_transactions;"),

    INSERT_NEW_ACCOUNT("INSERT INTO accounts (key, name, timestamp) VALUES ('%s', '%s', '%s');"),

    INSERT_NEW_TRANSACTION("INSERT INTO transactions (accountkey, timestamp, amount, description) " +
            "VALUES ('%s', '%s', '%s', '%s');"),

    CLOSE_ACCOUNT("INSERT INTO account_closures (key, timestamp) VALUES ('%s', '%s');");

    private final String sql;

    SqliteQuery(String sql) {
       this.sql = sql;
    }

    public String query() {
        return sql;
    }
}
