package main.usecase.eventing;

import main.domain.Transaction;

import java.util.ArrayList;
import java.util.Collection;

public interface TransactionListener {

    void onTransactionsEvent(Event<Collection<Transaction>> event);

    default void onTransactionEvent(Event<Transaction> event) {
        Collection<Transaction> transactions = new ArrayList<>();
        transactions.add(event.getData());
        onTransactionsEvent(new Event<>(event.getTimestamp(), event.getPredicate(), transactions));
    }
}
