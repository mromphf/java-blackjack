package main.usecase;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Collection;
import java.util.Set;

public interface MemoryListener {
    void onAccountsLoaded(Collection<Account> accounts);
    void onTransactionsLoaded(Set<Transaction> transactions);
}
