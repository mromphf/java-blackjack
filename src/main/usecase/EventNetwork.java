package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Transaction;
import main.io.EventConnection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EventNetwork implements
        AccountListener,
        ActionListener,
        MemoryListener,
        TransactionListener {

    private final Collection<AccountListener> accountListeners = new LinkedList<>();
    private final Collection<ActionListener> actionListeners = new LinkedList<>();
    private final Collection<MemoryListener> memoryListeners = new LinkedList<>();
    private final Collection<TransactionListener> transactionListeners = new LinkedList<>();
    private final Collection<EventListener> eventListeners = new LinkedList<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
            if (connection instanceof AccountListener) {
                accountListeners.add((AccountListener) connection);
            }

            if (connection instanceof ActionListener) {
                actionListeners.add((ActionListener) connection);
            }

            if (connection instanceof MemoryListener) {
                memoryListeners.add((MemoryListener) connection);
            }

            if (connection instanceof TransactionListener) {
                transactionListeners.add((TransactionListener) connection);
            }

            if (connection instanceof EventListener) {
                eventListeners.add((EventListener) connection);
            }
        }
    }

    public void registerEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void post(Event e) {
        eventListeners.forEach(l -> l.listen(e));
    }

    @Override
    public void onNewAccountOpened(Account account, int signingBonus) {
        accountListeners.forEach(l -> l.onNewAccountOpened(account, signingBonus));
    }

    @Override
    public void onAccountDeleted(Account account) {
        accountListeners.forEach(l -> l.onAccountDeleted(account));
    }

    @Override
    public void onActionTaken(Action action) {
        actionListeners.forEach(l -> l.onActionTaken(action));
    }

    @Override
    public void onBetPlaced(int amount) {
        actionListeners.forEach(l -> l.onBetPlaced(amount));
    }

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {
        memoryListeners.forEach(l -> l.onAccountsLoaded(accounts));
    }

    @Override
    public void onTransactionsLoaded(List<Transaction> transactions) {
        memoryListeners.forEach(l -> l.onTransactionsLoaded(transactions));
    }

    @Override
    public void onTransaction(Transaction transaction) {
        transactionListeners.forEach(l -> l.onTransaction(transaction));
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        transactionListeners.forEach(l -> l.onTransactions(transactions));
    }
}
