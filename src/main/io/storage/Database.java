package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static java.lang.String.format;
import static main.common.ResultSetUtil.accountFromResultSet;
import static main.common.ResultSetUtil.transactionFromResultSet;

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
                accounts.add(accountFromResultSet(rs));
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
    public Collection<Transaction> loadAllTransactions(Collection<Account> openAccounts) {
        try {
            final ArrayList<Transaction> transactions = new ArrayList<>();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery("SELECT accountkey, description, amount, TO_CHAR(timestamp AT TIME ZONE 'UTC', 'YYYY-MM-DDThh:mm:SS-06:00') AS timestamp FROM blackjack.transactions;");

            while (rs.next()) {
                transactions.add(transactionFromResultSet(rs));
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
    public void openNewAccount(Account account) {
        final String sql = format("INSERT INTO blackjack.accounts (key, name, timestamp) VALUES ('%s', '%s', '%s');",
                account.getKey(), account.getName(), account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void closeAccount(Account account) {
        final String sql = format("INSERT INTO blackjack.account_closures (key, timestamp) VALUES ('%s', '%s');",
                account.getKey(), account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String sql = format("INSERT INTO blackjack.transactions (accountkey, timestamp, amount, description) VALUES ('%s', '%s', '%s', '%s');",
                transaction.getAccountKey(), transaction.getTime(), transaction.getAmount(), transaction.getDescription());

        executePreparedStatement(sql);
    }

    private void executePreparedStatement(String sql) {
        try {
            final PreparedStatement st = conn.prepareStatement(sql);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
