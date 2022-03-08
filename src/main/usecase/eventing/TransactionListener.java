package main.usecase.eventing;

import main.common.Identifiable;
import main.domain.Transaction;

import java.util.Collection;

public interface TransactionListener extends Identifiable {
    void onTransactionIssued(Transaction transaction);
    void onTransactionSeriesIssued(Collection<Transaction> transactions);
    default void onTransactionsLoaded(Collection<Transaction> transactions) {}
}
