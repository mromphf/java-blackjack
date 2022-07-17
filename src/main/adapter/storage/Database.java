package main.adapter.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;
import static main.adapter.injection.Bindings.QUERIES_PSQL;
import static main.adapter.injection.Bindings.QUERIES_SQLITE;
import static main.adapter.storage.QueryKey.*;
import static main.adapter.storage.ResultSetUtil.accountFromResultSet;
import static main.adapter.storage.ResultSetUtil.transactionFromResultSet;

public class Database implements AccountRepository, TransactionRepository {

    private final Map<QueryKey, String> queryMap;

    @Inject
    public Database(@Named(QUERIES_SQLITE) Map<QueryKey, String> queryMap) {
        this.queryMap = queryMap;
    }

    @Override
    public Collection<Account> loadAllAccounts() {
        try {
            final ArrayList<Account> accounts = new ArrayList<>();
            final Connection conn = openDbConnection();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery(queryMap.get(ALL_ACCOUNTS));

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
            final ResultSet rs = st.executeQuery(queryMap.get(ALL_TRANSACTIONS));

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
        final String sql = format(queryMap.get(CREATE_NEW_ACCOUNT),
                account.getKey(),
                account.getName(),
                account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void closeAccount(Account account) {
        final String sql = format(queryMap.get(DELETE_ACCOUNT),
                account.getKey(),
                account.getCreated());

        executePreparedStatement(sql);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String sql = format(queryMap.get(CREATE_NEW_TRANSACTION),
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

    private Connection openDbConnection() throws SQLException {
        final String url = queryMap.get(URL);
        final Properties props = new Properties();

        props.setProperty("user", "mromphf");

        return getConnection(url, props);
    }
}
