package main.io.storage;

import main.domain.Account;
import main.usecase.TransactionListener;

import java.util.ArrayDeque;
import java.util.Queue;

public class AccountStorage {

    private final Storage storage;
    private final Queue<TransactionListener> transactionListeners;

    public AccountStorage(Storage storage) {
        this.storage = storage;
        this.transactionListeners = new ArrayDeque<>();
    }

    public void registerTransactionListener(TransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    public void loadAllAccounts() {
        for(Account account: storage.loadAllAccounts()) {
            transactionListeners.forEach(l -> l.onBalanceChanged(account));
        }
    }
}
