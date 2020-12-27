package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.AccountListener;
import main.usecase.MemoryListener;

import java.time.LocalDateTime;
import java.util.*;

public class AccountStorage implements AccountListener {

    private final Memory memory;
    private final Queue<MemoryListener> memoryListeners;

    public AccountStorage(Memory memory) {
        this.memory = memory;
        this.memoryListeners = new ArrayDeque<>();
    }

    public void registerMemoryListener(MemoryListener memoryListener) {
        memoryListeners.add(memoryListener);
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = memory.loadAllAccounts();
        memoryListeners.forEach(l -> l.onAccountsLoaded(accounts));
    }

    public void loadAllTransactions() {
        final List<Transaction> transactions = memory.loadAllTransactions();
        memoryListeners.forEach(l -> l.onTransactionsLoaded(transactions));
    }

    @Override
    public void onNewAccountOpened(Account account, int signingBonus) {
        final Transaction t = new Transaction(
                LocalDateTime.now(),
                account.getKey(),
                "SIGNING BONUS",
                signingBonus
        );

        memory.saveNewAccount(account);
        memory.saveTransaction(t);
    }

    @Override
    public void onAccountDeleted(Account account) {
        memory.deleteAccount(account);
    }

    @Override
    public void onTransaction(Transaction transaction) {
        memory.saveTransaction(transaction);
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        memory.saveTransactions(transactions);
    };

    @Override
    public void onAccountUpdated(Account account) {}
}
