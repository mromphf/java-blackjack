package main.usecase.eventing;

import javafx.scene.control.Alert;
import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.Layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class EventNetwork implements
        SnapshotListener,
        LayoutListener,
        AccountListener,
        TransactionListener,
        AlertListener {

    private final UUID key;
    private final Collection<AccountListener> accountListeners = new ArrayList<>();
    private final Collection<LayoutListener> layoutListeners = new ArrayList<>();
    private final Collection<TransactionListener> transactionListeners = new ArrayList<>();
    private final Collection<SnapshotListener> snapshotListeners = new LinkedList<>();
    private final Collection<AlertListener> alertListeners = new ArrayList<>();

    public EventNetwork(UUID key) {
        this.key = key;
    }

    public void registerListeners(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
            if (connection instanceof SnapshotListener) {
                snapshotListeners.add((SnapshotListener) connection);
            }

            if (connection instanceof LayoutListener) {
                layoutListeners.add((LayoutListener) connection);
            }

            if (connection instanceof AccountListener) {
                accountListeners.add((AccountListener) connection);
            }

            if (connection instanceof TransactionListener) {
                transactionListeners.add((TransactionListener) connection);
            }

            if (connection instanceof AlertListener) {
                alertListeners.add((AlertListener) connection);
            }
        }
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        snapshotListeners.forEach(listener -> new Thread(
                () -> listener.onGameUpdate(snapshot), "Snapshot Thread"
        ).start());
    }

    @Override
    public void onLayoutEvent(Layout event) {
        layoutListeners.forEach(listener -> listener.onLayoutEvent(event));
    }

    @Override
    public void onAccountCreated(Account account) {
        accountListeners.forEach(a -> a.onAccountCreated(account));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        accountListeners.stream()
                .filter(listener -> !listener.getKey().equals(event.getKey()))
                .forEach(listener -> new Thread(
                        () -> listener.onAccountEvent(event), "Account Event Thread"
                ).start());
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        accountListeners.stream()
                .filter(listener -> !listener.getKey().equals(event.getKey()))
                .forEach(listener -> new Thread(
                        () -> listener.onAccountsEvent(event), "Accounts Event Thread"
                ).start());
    }

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {
        accountListeners.forEach(listener -> new Thread(
                () -> listener.onAccountsLoaded(accounts), "Accounts Event Thread"
        ).start());
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        transactionListeners.forEach(t -> t.onTransactionIssued(transaction));
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactionListeners.forEach(t -> t.onTransactionSeriesIssued(transactions));
    }

    @Override
    public void onTransactionsLoaded(Collection<Transaction> transactions) {
        transactionListeners.forEach(t -> t.onTransactionsLoaded(transactions));
    }

    @Override
    public void onAlertEvent(Alert event) {
        alertListeners.forEach(listener -> listener.onAlertEvent(event));
    }
}
