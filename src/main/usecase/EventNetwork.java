package main.usecase;

import main.domain.Transaction;
import main.io.EventConnection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EventNetwork implements TransactionListener {

    private final Collection<TransactionListener> transactionListeners = new LinkedList<>();
    private final Collection<EventListener> eventListeners = new LinkedList<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
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
    public void onTransaction(Transaction transaction) {
        transactionListeners.forEach(l -> l.onTransaction(transaction));
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        transactionListeners.forEach(l -> l.onTransactions(transactions));
    }
}
