package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static main.usecase.eventing.Predicate.*;


public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final UUID key;
    private final Memory memory;

    public AccountStorage(UUID key, Memory memory) {
        this.key = key;
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = memory.loadAllAccounts();
        final Event<Collection<Account>> event = new Event<>(key, now(), ACCOUNTS_LOADED, accounts);

        eventNetwork.onAccountsEvent(event);
    }

    public void loadAllTransactions() {
        final List<Transaction> transactions = memory.loadAllTransactions();
        final Event<Collection<Transaction>> event = new Event<>(key, now(), TRANSACTIONS_LOADED, transactions);

        eventNetwork.onTransactionsEvent(event);
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            memory.saveTransaction(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            memory.saveTransactions(event.getData());
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            memory.openAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            memory.closeAccount(event.getData());
        }
    }
}
