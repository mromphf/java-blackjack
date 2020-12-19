package main.io.storage;

import main.domain.Account;
import main.usecase.TransactionListener;

import java.util.ArrayDeque;
import java.util.Queue;

public class AccountStorage {

    private final SaveFile saveFile;
    private final Queue<TransactionListener> transactionListeners;

    public AccountStorage(SaveFile saveFile) {
        this.saveFile = saveFile;
        this.transactionListeners = new ArrayDeque<>();
    }

    public void registerTransactionListener(TransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    public void loadAllAccounts() {
        for(Account account: saveFile.getAllAccounts()) {
            transactionListeners.forEach(l -> l.onBalanceChanged(account));
        }
    }
}
