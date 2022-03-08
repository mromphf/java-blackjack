package main.usecase.eventing;

import main.domain.Transaction;

import java.util.Collection;

public interface TransactionListener {
    default void onTransactionIssued(Transaction transaction) {}
    default void onTransactionSeriesIssued(Collection<Transaction> transactions) {}
    default void onTransactionsLoaded(Collection<Transaction> transactions) {}
}
