package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.util.Collection;
import java.util.List;

import static main.usecase.eventing.Predicate.*;


public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Event<Collection<Account>> event = new Event<>(ACCOUNTS_LOADED, memory.loadAllAccounts());
        eventNetwork.onAccountsEvent(event);
    }

    public void loadAllTransactions() {
        final Event<List<Transaction>> event = new Event<>(TRANSACTIONS_LOADED, memory.loadAllTransactions());
        eventNetwork.onTransactionsEvent(event);
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        memory.saveTransaction(event.getData());
    }

    @Override
    public void onTransactionsEvent(Event<List<Transaction>> event) {
        memory.saveTransactions(event.getData());
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            memory.saveNewAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            memory.deleteAccount(event.getData());
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getPredicate(), account)));
    }
}
