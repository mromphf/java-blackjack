package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.AccountListener;
import main.usecase.MemoryListener;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

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

    @Override
    public void onNewAccountOpened(Account account, int signingBonus) {
        final Transaction t = new Transaction(
                LocalDateTime.now(),
                account.getKey(),
                "Signing Bonus",
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
    public void onAccountUpdated(Account account) {}
}
