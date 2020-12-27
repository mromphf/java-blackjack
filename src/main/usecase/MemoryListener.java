package main.usecase;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Collection;
import java.util.List;

public interface MemoryListener {
    void onAccountsLoaded(Collection<Account> accounts);
    void onTransactionsLoaded(List<Transaction> transactions);
}
