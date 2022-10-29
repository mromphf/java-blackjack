package com.blackjack.main.adapter.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.blackjack.main.domain.model.*;
import com.blackjack.main.usecase.AccountRepository;
import com.blackjack.main.usecase.StateRepository;
import com.blackjack.main.usecase.TransactionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static java.lang.System.exit;
import static java.sql.DriverManager.getConnection;
import static com.blackjack.main.adapter.injection.Bindings.QUERIES_SQLITE;
import static com.blackjack.main.adapter.storage.QueryKey.*;
import static com.blackjack.main.adapter.storage.ResultSetDeserializer.accountFromResultSet;
import static com.blackjack.main.adapter.storage.ResultSetDeserializer.transactionFromResultSet;
import static com.blackjack.main.adapter.storage.SqliteQuery.INSERT_DEALER_CARD;

public class Database implements AccountRepository, TransactionRepository, StateRepository {

    private final Map<QueryKey, String> queryMap;

    @Inject
    public Database(@Named(QUERIES_SQLITE) Map<QueryKey, String> queryMap) {
        this.queryMap = queryMap;
    }

    @Override
    public Collection<Account> loadAllAccounts() {
        try (final Connection conn = openDbConnection()) {
            final ArrayList<Account> accounts = new ArrayList<>();
            final Statement st = conn.createStatement();
            final ResultSet rs = st.executeQuery(queryMap.get(ALL_ACCOUNTS));

            while (rs.next()) {
                accounts.add(accountFromResultSet(rs));
            }

            return accounts;

        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
            return null;
        }
    }

    @Override
    public void createNew(Account account) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(queryMap.get(CREATE_NEW_ACCOUNT));

            st.setString(1, account.key().toString());
            st.setString(2, account.getName());
            st.setString(3, account.getTimestamp().toString());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    @Override
    public Collection<Transaction> loadAllTransactions() {
        try (final Connection conn = openDbConnection()) {
            final ArrayList<Transaction> transactions = new ArrayList<>();
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
            exit(1);

            return null;
        }
    }

    @Override
    public void closeAccount(Account account) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(DELETE_ACCOUNT));

            st.setString(1, account.key().toString());
            st.setString(2, account.getTimestamp().toString());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(CREATE_NEW_TRANSACTION));

            st.setString(1, transaction.accountKey().toString());
            st.setString(2, transaction.timestamp().toString());
            st.setInt(3, transaction.amount());
            st.setString(4, transaction.description());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveNewRound(TableView tableView) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(CREATE_NEW_ROUND));

            st.setString(1, tableView.roundKey().toString());
            st.setString(2, tableView.timestamp().toString());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveNewDeck(TableView tableView) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(
                    queryMap.get(CREATE_NEW_DECK));

            st.setString(1, tableView.roundKey().toString());
            st.setString(2, tableView.deckKey().toString());

            st.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveNewCards(Deck deck) {
        try (Connection conn = openDbConnection()) {
            final String query = "INSERT INTO cards (deckKey, cardKey, ordinal, suit, rank) VALUES ";
            final StringBuilder body = new StringBuilder();

            for (Card card : deck) {
                body.append(format("('%s','%s',%s,'%s',%s),",
                        deck.key(), card.key(), card.ordinal(), card.suit(), card.rank().ORDINAL));
            }

            // SQL needs us to chop trailing comma from the last record
            body.deleteCharAt(body.length() - 1);

            final String sql = format("%s\n%s;", query, body);
            final PreparedStatement st = conn.prepareStatement(sql);

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }


    @Override
    public void saveCardDrawn(LocalDateTime timestamp,
                              UUID handKey,
                              UUID cardKey,
                              UUID accountKey,
                              UUID roundKey) {
        try (Connection conn = openDbConnection()) {
            final String query = queryMap.get(SAVE_CARD_DRAWN);
            final PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, handKey.toString());
            st.setString(2, cardKey.toString());
            st.setString(3, accountKey.toString());
            st.setString(4, roundKey.toString());
            st.setString(5, timestamp.toString());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveDealerCard(LocalDateTime timestamp, UUID cardKey) {
        try (final Connection conn = openDbConnection()) {
            final PreparedStatement st = conn.prepareStatement(INSERT_DEALER_CARD.query());

            st.setString(1, timestamp.toString());
            st.setString(2, cardKey.toString());

            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void saveLastActionTaken(TableView tableView) {
        try (final Connection conn = openDbConnection()) {
            if (tableView.lastActionTaken().isPresent()) {
                final PreparedStatement st = conn.prepareStatement(
                        queryMap.get(SAVE_ACTION));

                st.setString(1, tableView.timestamp().toString());
                st.setString(2, tableView.roundKey().toString());
                st.setString(3, tableView.playerAccountKey().toString());
                st.setString(4, tableView.lastActionTaken().get().name());

                st.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    private Connection openDbConnection() throws SQLException {
        final String url = queryMap.get(URL);
        final Properties props = new Properties();

        props.setProperty("user", "mromphf");

        return getConnection(url, props);
    }
}
