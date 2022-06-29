package main.adapter.storage;

import main.domain.model.Transaction;

import java.util.Collection;

public interface TransactionRepository {
    Collection<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    /*
        It would be performant to save a collection of transactions in one
        query, but for now most use-cases should only yield an average of one
        transaction every few seconds.
        - MR June 28 2022
     */
}
