package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.util.List;
import java.util.Set;

public interface Memory {
    Set<Account> loadAllAccounts();
    List<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    void saveTransactions(List<Transaction> transactions);
    void saveNewAccount(Account account);
    void deleteAccount(Account account);
}
