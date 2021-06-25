package main.usecase.eventing;

import main.domain.Identifiable;
import main.domain.Transaction;

import java.util.ArrayList;
import java.util.Collection;

public interface TransactionListener extends Identifiable {

    void onTransactionsEvent(Event<Collection<Transaction>> event);

    default void onTransactionEvent(Event<Transaction> event) {
        Collection<Transaction> transactions = new ArrayList<>();
        transactions.add(event.getData());
        onTransactionsEvent(new Event<>(event.getSourceKey(), event.getTimestamp(), event.getPredicate(), transactions));
    }
}
