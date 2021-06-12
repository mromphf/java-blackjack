package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.io.EventConnection;

import java.util.*;

public class EventNetwork implements
        ActionListener,
        BalanceListener,
        GameStateListener,
        MemoryListener,
        NavListener,
        TransactionListener,
        Responder,
        EventListener {

    private final Collection<ActionListener> actionListeners = new LinkedList<>();
    private final Collection<BalanceListener> balanceListeners = new LinkedList<>();
    private final Collection<GameStateListener> gameStateListeners = new LinkedList<>();
    private final Collection<MemoryListener> memoryListeners = new LinkedList<>();
    private final Collection<NavListener> navListeners = new LinkedList<>();
    private final Collection<TransactionListener> transactionListeners = new LinkedList<>();
    private final Collection<EventListener> eventListeners = new LinkedList<>();
    private final Map<NetworkElement, Responder> responders = new HashMap<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
            if (connection instanceof ActionListener) {
                actionListeners.add((ActionListener) connection);
            }

            if (connection instanceof BalanceListener) {
                balanceListeners.add((BalanceListener) connection);
            }

            if (connection instanceof GameStateListener) {
                gameStateListeners.add((GameStateListener) connection);
            }

            if (connection instanceof MemoryListener) {
                memoryListeners.add((MemoryListener) connection);
            }

            if (connection instanceof NavListener) {
                navListeners.add((NavListener) connection);
            }

            if (connection instanceof TransactionListener) {
                transactionListeners.add((TransactionListener) connection);
            }

            if (connection instanceof EventListener) {
                eventListeners.add((EventListener) connection);
            }
        }
    }

    public void registerResponder(NetworkElement elm, Responder responder) {
        responders.put(elm, responder);
    }

    public void registerGameStateListener(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    public void registerBalanceListener(BalanceListener listener) {
        balanceListeners.add(listener);
    }

    public void registerTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }

    public void registerEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    @Override
    public void onEvent(Message message) {
        eventListeners.forEach(l -> l.onEvent(message));
    }

    @Override
    public void onActionTaken(Action action) {
        actionListeners.forEach(l -> l.onActionTaken(action));
    }

    @Override
    public void onBetPlaced(Account account, int amount) {
        actionListeners.forEach(l -> l.onBetPlaced(account, amount));
    }

    @Override
    public void onBalanceUpdated() {
        balanceListeners.forEach(BalanceListener::onBalanceUpdated);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        gameStateListeners.forEach(l -> l.onUpdate(snapshot));
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
    public void onChangeLayout(Layout layout) {
        navListeners.forEach(l -> l.onChangeLayout(layout));
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        navListeners.forEach(l -> l.onChangeLayout(layout, account));
    }

    @Override
    public void onTransaction(Transaction transaction) {
        transactionListeners.forEach(l -> l.onTransaction(transaction));
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        transactionListeners.forEach(l -> l.onTransactions(transactions));
    }

    @Override
    public Message fulfill(NetworkElement elm) {
        return responders.get(elm).fulfill(elm);
    }
}
