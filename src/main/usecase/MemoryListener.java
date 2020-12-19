package main.usecase;

import main.domain.Account;

import java.util.Collection;

public interface MemoryListener {
    void onAccountsLoaded(Collection<Account> accounts);
}
