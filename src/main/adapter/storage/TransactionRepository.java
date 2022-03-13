package main.adapter.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Collection;

public interface TransactionRepository {
    Collection<Transaction> loadAllTransactions(Collection<Account> openAccounts);
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }
}
