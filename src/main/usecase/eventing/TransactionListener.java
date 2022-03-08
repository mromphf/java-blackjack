package main.usecase.eventing;

import main.domain.Transaction;

import java.util.Collection;

public interface TransactionListener {
    void onTransactionIssued(Transaction transaction);
    void onTransactionSeriesIssued(Collection<Transaction> transactions);
    default void onTransactionsLoaded(Collection<Transaction> transactions) {}
}
