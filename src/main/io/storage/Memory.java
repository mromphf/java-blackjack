package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Set;

public interface Memory {
    Set<Account> loadAllAccounts();
    Set<Transaction> loadAllTransactions();
    void saveNewAccount(Account account);
    void deleteAccount(Account account);
}
