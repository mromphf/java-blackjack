package main.adapter.storage;

import main.domain.Transaction;

import java.util.Collection;

public interface TransactionRepository {
    Collection<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }
}
