package main.usecase;

import main.domain.model.Transaction;

import java.util.Collection;

public interface TransactionListener {
    default void onTransactionIssued(Transaction transaction) {}
    default void onTransactionsLoaded(Collection<Transaction> transactions) {}
}
