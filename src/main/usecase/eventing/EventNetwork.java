package main.usecase.eventing;

import javafx.scene.control.Alert;
import main.domain.*;
import main.io.EventConnection;
import main.usecase.Layout;

import java.util.*;

import static main.usecase.eventing.Predicate.TRANSACTION;

public class EventNetwork implements
        SnapshotListener,
        AccountResponder,
        BetListener,
        LayoutListener,
        ActionListener,
        AccountListener,
        TransactionListener,
        TransactionResponder,
        AlertListener {

    private final Collection<AccountListener> accountListeners = new ArrayList<>();
    private final Collection<BetListener> betListeners = new ArrayList<>();
    private final Collection<LayoutListener> layoutListeners = new ArrayList<>();
    private final Collection<ActionListener> actionListeners = new ArrayList<>();
    private final Collection<TransactionListener> transactionListeners = new ArrayList<>();
    private final Collection<SnapshotListener> snapshotListeners = new LinkedList<>();
    private final Collection<AlertListener> alertListeners = new ArrayList<>();
    private final Map<Predicate, AccountResponder> accountResponders = new HashMap<>();
    private final Map<Predicate, TransactionResponder> transactionResponders = new HashMap<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
            if (connection instanceof SnapshotListener) {
                snapshotListeners.add((SnapshotListener) connection);
            }

            if (connection instanceof BetListener) {
                betListeners.add((BetListener) connection);
            }

            if (connection instanceof LayoutListener) {
                layoutListeners.add((LayoutListener) connection);
            }

            if (connection instanceof ActionListener) {
                actionListeners.add((ActionListener) connection);
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

    public void registerResponder(Predicate predicate, AccountResponder accountResponder) {
        accountResponders.put(predicate, accountResponder);
    }

    public void registerResponder(Predicate predicate, TransactionResponder transactionResponder) {
        transactionResponders.put(predicate, transactionResponder);
    }

    public void registerGameStateListener(SnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    public void registerTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }

    public void registerAccountListener(AccountListener listener) {
        accountListeners.add(listener);
    }

    public void registerActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        snapshotListeners.forEach(l -> l.onGameUpdate(snapshot));
    }

    @Override
    public Account requestSelectedAccount(Predicate elm) {
        return accountResponders.get(elm).requestSelectedAccount(elm);
    }

    @Override
    public Collection<Transaction> requestTransactionsByKey(UUID accountKey) {
        return transactionResponders.get(TRANSACTION).requestTransactionsByKey(accountKey);
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        betListeners.forEach(listener -> listener.onBetEvent(event));
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        layoutListeners.forEach(listener -> listener.onLayoutEvent(event));
    }

    @Override
    public void onActionEvent(Event<Action> event) {
        actionListeners.forEach(listener -> listener.onActionEvent(event));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        accountListeners.forEach(listener -> listener.onAccountEvent(event));
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        accountListeners.forEach(listener -> listener.onAccountsEvent(event));
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        transactionListeners.forEach(listener -> listener.onTransactionEvent(event));
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        transactionListeners.forEach(listener -> listener.onTransactionsEvent(event));
    }

    @Override
    public void onAlertEvent(Event<Alert> event) {
        alertListeners.forEach(listener -> listener.onAlertEvent(event));
    }
}
