package main.adapter.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountRepository;
import main.usecase.TransactionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;
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
    public void createNew(Account account) {
        try {
            final Connection conn = openDbConnection();
            final PreparedStatement st = conn.prepareStatement(queryMap.get(CREATE_NEW_ACCOUNT));

            st.setString(1, account.getKey().toString());
            st.setString(2, account.getName());
            st.setString(3, account.getCreated().toString());

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
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
    public void closeAccount(Account account) {
        try {
            final Connection conn = openDbConnection();
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(DELETE_ACCOUNT));

            st.setString(1, account.getKey().toString());
            st.setString(2, account.getCreated().toString());

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        try {
            final Connection conn = openDbConnection();
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(CREATE_NEW_TRANSACTION));

            st.setString(1, transaction.getAccountKey().toString());
            st.setString(2, transaction.getTime().toString());
            st.setInt(3, transaction.getAmount());
            st.setString(4, transaction.getDescription());

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
