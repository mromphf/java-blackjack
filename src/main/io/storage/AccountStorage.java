package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.usecase.eventing.Predicate.*;


public class AccountStorage extends EventConnection implements EventListener, AccountListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        eventNetwork.onAccountsEvent(new Event<>(ACCOUNTS_LOADED, memory.loadAllAccounts()));
    }

    public void loadAllTransactions() {
        final Message message = Message.of(TRANSACTIONS_LOADED, memory.loadAllTransactions());
        eventNetwork.onEvent(message);
    }

    @Override
    public void onEvent(Message message) {
        final Map<Predicate, Runnable> runnableMap = new HashMap<>();

        runnableMap.put(TRANSACTION, () -> saveTransaction(message.getTransaction()));
        runnableMap.put(TRANSACTION_SERIES, () -> saveTransactions(message.getTransactions()));

        runnableMap.getOrDefault(message.getPredicate(), () -> {}).run();
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            memory.saveNewAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            memory.deleteAccount(event.getData());
        }
    }

    private void saveTransaction(Transaction transaction) {
        memory.saveTransaction(transaction);
    }

    private void saveTransactions(List<Transaction> transactions) {
        memory.saveTransactions(transactions);
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {

    }
}
