package main.io.storage;

import com.google.inject.Inject;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.*;

import static java.time.LocalDateTime.now;
import static main.usecase.eventing.Predicate.*;


public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final UUID key = UUID.randomUUID();
    private final TransactionMemory transactionMemory;
    private final AccountMemory accountMemory;

    @Inject
    public AccountStorage(TransactionMemory transactionMemory, AccountMemory accountMemory) {
        this.transactionMemory = transactionMemory;
        this.accountMemory = accountMemory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = accountMemory.loadAllAccounts(new ArrayList<>());
        final Event<Collection<Account>> event = new Event<>(key, now(), ACCOUNTS_LOADED, accounts);
        eventNetwork.onAccountsEvent(event);
    }

    public void loadAllTransactions() {
        final Collection<Transaction> allTransactions = transactionMemory.loadAllTransactions(new ArrayList<>());
        final Event<Collection<Transaction>> transEvent = new Event<>(key, now(), TRANSACTIONS_LOADED, allTransactions);
        eventNetwork.onTransactionsEvent(transEvent);
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            transactionMemory.saveTransaction(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            transactionMemory.saveTransactions(event.getData());
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            accountMemory.openNewAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            accountMemory.closeAccount(event.getData());
        }
    }
}
