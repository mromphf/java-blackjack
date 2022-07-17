package main.adapter.storage;

import main.domain.model.Account;
import main.domain.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;
import static main.adapter.storage.ResultSetUtil.accountFromResultSet;
import static main.adapter.storage.ResultSetUtil.transactionFromResultSet;
import static main.adapter.storage.SqliteQuery.*;

public class SqliteDatabase implements AccountRepository, TransactionRepository {


    @Override
    public Collection<Account> loadAllAccounts() {
        try {
            final ArrayList<Account> accounts = new ArrayList<>();
            final Connection conn = openDbConnection();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery(SELECT_ALL_ACCOUNTS.sql);

            while (rs.next()) {
                accounts.add(accountFromResultSet(rs));
            }

            rs.close();
            st.close();
            conn.close();

            return accounts;

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public Collection<Transaction> loadAllTransactions() {
        try {
            final ArrayList<Transaction> transactions = new ArrayList<>();
            final Connection conn = openDbConnection();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery(SELECT_ALL_TRANSACTIONS.sql);

            while (rs.next()) {
                transactions.add(transactionFromResultSet(rs));
            }

            rs.close();
            st.close();
            conn.close();

            return transactions;

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);

            return null;
        }
    }

    @Override
    public void createNew(Account account) {
        final String sql = format(INSERT_NEW_ACCOUNT.sql,
                account.getKey(),
                account.getName(),
                account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void closeAccount(Account account) {
        final String sql = format(CLOSE_ACCOUNT.sql,
                account.getKey(),
                account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String sql = format(INSERT_NEW_TRANSACTION.sql,
                transaction.getAccountKey(),
                transaction.getTime(),
                transaction.getAmount(),
                transaction.getDescription());

        executePreparedStatement(sql);
    }

    private void executePreparedStatement(String sql) {
        try {
            final Connection conn = openDbConnection();
            final PreparedStatement st = conn.prepareStatement(sql);

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Connection openDbConnection() throws SQLException {
        final String url = "jdbc:sqlite:/Users/mromphf/data/blackjack.db";

        return getConnection(url);
    }
}
