package main.usecase.eventing;

import main.domain.Transaction;

import java.util.List;

public interface TransactionListener {
    void onTransactionEvent(Event<Transaction> event);
    void onTransactionsEvent(Event<List<Transaction>> event);
}
