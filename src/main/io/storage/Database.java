package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class Database implements AccountMemory, TransactionMemory {

    private final Connection conn;

    public Database(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Collection<Account> loadAllAccounts(Collection<UUID> closedKeys) {
        try {
            final ArrayList<Account> accounts = new ArrayList<>();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery("SELECT * FROM blackjack.account_balances;");
            while (rs.next()) {

                final UUID key = UUID.fromString(rs.getString("key"));
                final String name = rs.getString("name");
                final int balance = rs.getInt("balance");
                final ZonedDateTime timestamp = ZonedDateTime.parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME);

                accounts.add(new Account(key, name, balance, timestamp.toLocalDateTime()));
            }

            rs.close();
            st.close();

            return accounts;

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);

            return null;
        }
    }

    @Override
    public void openNewAccount(Account account) {
        try {
            final String sql = format("INSERT INTO blackjack.accounts (key, name, timestamp) VALUES ('%s', '%s', '%s');",
                    account.getKey(), account.getName(), account.getCreated());

            final PreparedStatement st = conn.prepareStatement(sql);

            st.executeUpdate();

            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void closeAccount(Account account) {
        try {
            final String sql = format("INSERT INTO blackjack.account_closures (key, timestamp) VALUES ('%s', '%s');",
                    account.getKey(), account.getCreated());

            final PreparedStatement st = conn.prepareStatement(sql);

            st.executeUpdate();

            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Collection<Transaction> loadAllTransactions(Collection<Account> openAccounts) {
        try {
            final ArrayList<Transaction> transactions = new ArrayList<>();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery("SELECT accountkey, description, amount, TO_CHAR(timestamp AT TIME ZONE 'UTC', 'YYYY-MM-DDThh:mm:SS-06:00') AS timestamp FROM blackjack.transactions;");
            while (rs.next()) {

                final UUID key = UUID.fromString(rs.getString("accountkey"));
                final String description = rs.getString("description");
                final int amount = rs.getInt("amount");
                final ZonedDateTime timestamp = ZonedDateTime.parse(rs.getString("timestamp"), ISO_OFFSET_DATE_TIME);

                transactions.add(new Transaction(timestamp.toLocalDateTime(), key, description, amount));
            }

            rs.close();
            st.close();

            return transactions;

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);

            return null;
        }
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        try {
            final String sql = format("INSERT INTO blackjack.transactions (accountkey, timestamp, amount, description) VALUES ('%s', '%s', '%s', '%s');",
                    transaction.getAccountKey(), transaction.getTime(), transaction.getAmount(), transaction.getDescription());

            final PreparedStatement st = conn.prepareStatement(sql);

            st.executeUpdate();

            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
