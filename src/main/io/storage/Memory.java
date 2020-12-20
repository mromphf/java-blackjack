package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Set;

public interface Memory {
    Set<Account> loadAllAccounts();
    Set<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    void saveTransactions(Set<Transaction> transactions);
    void saveNewAccount(Account account);
    void deleteAccount(Account account);
}
