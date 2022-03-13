package main.adapter.storage;

import main.domain.Account;

import java.util.Collection;
import java.util.UUID;

public interface AccountRepository {
    Collection<Account> loadAllAccounts(Collection<UUID> closedKeys);
    void openNewAccount(Account account);
    void closeAccount(Account account);
}
