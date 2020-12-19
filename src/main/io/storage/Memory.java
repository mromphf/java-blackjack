package main.io.storage;

import main.domain.Account;

import java.util.Set;

public interface Memory {
    Set<Account> loadAllAccounts();
    void saveNewAccount(Account account);
}
