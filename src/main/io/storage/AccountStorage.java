package main.io.storage;

import main.domain.Account;
import main.usecase.AccountListener;
import main.usecase.MemoryListener;

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
    public void onNewAccountOpened(Account account) {
        memory.saveNewAccount(account);
    }

    @Override
    public void onBalanceChanged(Account account) {}
}
