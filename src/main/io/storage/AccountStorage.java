package main.io.storage;

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

    private final UUID key;
    private final TransactionMemory transactionMemory;
    private final AccountMemory accountMemory;

    public AccountStorage(UUID key, TransactionMemory transactionMemory, AccountMemory accountMemory) {
        this.key = key;
        this.transactionMemory = transactionMemory;
        this.accountMemory = accountMemory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = accountMemory.loadAllAccounts(new ArrayList<>());
        final Collection<Transaction> allTransactions = transactionMemory.loadAllTransactions(accounts);

        final Event<Collection<Transaction>> transEvent = new Event<>(key, now(), TRANSACTIONS_LOADED, allTransactions);
        final Event<Collection<Account>> event = new Event<>(key, now(), ACCOUNTS_LOADED, accounts);

        eventNetwork.onAccountsEvent(event);
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
