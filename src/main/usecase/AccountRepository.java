package main.usecase;

import main.domain.model.Account;

import java.util.Collection;

public interface AccountRepository {
    Collection<Account> loadAllAccounts();
    void createNew(Account account);
    void closeAccount(Account account);
}
