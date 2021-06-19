package main.usecase.eventing;

import main.domain.Transaction;

import java.util.Collection;

public interface TransactionListener {
    void onTransactionEvent(Event<Transaction> event);
    void onTransactionsEvent(Event<Collection<Transaction>> event);
}
