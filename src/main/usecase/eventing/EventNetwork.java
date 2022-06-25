package main.usecase.eventing;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.scene.control.Alert;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;
import main.usecase.Layout;

import java.util.Collection;

import static main.adapter.injection.Bindings.*;

public class EventNetwork implements
        SnapshotListener,
        LayoutListener,
        AccountListener,
        TransactionListener,
        AlertListener {

    private final Collection<AccountListener> accountListeners;
    private final Collection<AlertListener> alertListeners;
    private final Collection<LayoutListener> layoutListeners;
    private final Collection<SnapshotListener> snapshotListeners;
    private final Collection<TransactionListener> transactionListeners;

    @Inject
    public EventNetwork(
            @Named(ACCOUNT_LISTENERS) Collection<AccountListener> accountListeners,
            @Named(ALERT_LISTENERS) Collection<AlertListener> alertListeners,
            @Named(SNAPSHOT_LISTENERS) Collection<SnapshotListener> snapshotListeners,
            @Named(LAYOUT_LISTENERS) Collection<LayoutListener> layoutListeners,
            @Named(TRANSACTION_LISTENERS) Collection<TransactionListener> transactionListeners) {
        this.accountListeners = accountListeners;
        this.alertListeners = alertListeners;
        this.snapshotListeners = snapshotListeners;
        this.transactionListeners = transactionListeners;
        this.layoutListeners = layoutListeners;
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
    public void onAccountDeleted(Account account) {
        accountListeners.forEach(a -> a.onAccountDeleted(account));
    }

    @Override
    public void onAccountSelected(Account account) {
        accountListeners.forEach(a -> a.onAccountSelected(account));
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
