package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.EventListener;
import main.usecase.Message;
import main.usecase.Predicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.usecase.Predicate.*;


public class AccountStorage extends EventConnection implements EventListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Message message = Message.of(ACCOUNTS_LOADED, memory.loadAllAccounts());
        eventNetwork.onEvent(message);
    }

    public void loadAllTransactions() {
        final Message message = Message.of(TRANSACTIONS_LOADED, memory.loadAllTransactions());
        eventNetwork.onEvent(message);
    }

    @Override
    public void onEvent(Message message) {
        final Map<Predicate, Runnable> runnableMap = new HashMap<>();

        runnableMap.put(ACCOUNT_CREATED, () -> saveNewAccount(message.getAccount()));
        runnableMap.put(ACCOUNT_DELETED, () -> deleteAccount(message.getAccount()));
        runnableMap.put(TRANSACTION, () -> saveTransaction(message.getTransaction()));
        runnableMap.put(TRANSACTION_SERIES, () -> saveTransactions(message.getTransactions()));

        runnableMap.getOrDefault(message.getPredicate(), () -> {}).run();
    }

    private void saveNewAccount(Account account) {
        memory.saveNewAccount(account);
    }

    private void deleteAccount(Account account) {
        memory.deleteAccount(account);
    }

    private void saveTransaction(Transaction transaction) {
        memory.saveTransaction(transaction);
    }

    private void saveTransactions(List<Transaction> transactions) {
        memory.saveTransactions(transactions);
    }
}
