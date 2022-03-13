package main.adapter.storage;

import main.domain.Account;

import java.util.Collection;

public interface AccountRepository {
    Collection<Account> loadAllAccounts();
    void openNewAccount(Account account);
    void closeAccount(Account account);
}
