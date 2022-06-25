package main.adapter.storage;

import main.domain.model.Transaction;

import java.util.Collection;

public interface TransactionRepository {
    Collection<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }
}
