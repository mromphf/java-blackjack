package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.*;
import main.usecase.EventListener;

import java.time.LocalDateTime;
import java.util.*;

import static main.usecase.DataKey.ACCOUNT;
import static main.usecase.Predicate.*;

public class AccountStorage extends EventConnection implements EventListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = memory.loadAllAccounts();
        eventNetwork.post(Event.accountsLoaded(accounts));
    }

    public void loadAllTransactions() {
        final List<Transaction> transactions = memory.loadAllTransactions();
        eventNetwork.post(Event.transactionsLoaded(transactions));
    }

    @Override
    public void listen(Event e) {
        if (e.is(ACCOUNT_OPENED)) {
            final Account account = (Account) e.getData(ACCOUNT);
            final Transaction t = new Transaction(
                    LocalDateTime.now(),
                    account.getKey(),
                    "SIGNING BONUS",
                    200
            );

            memory.saveNewAccount(account);
            memory.saveTransaction(t);
        } else if (e.is(ACCOUNT_DELETED)) {
            final Account account = (Account) e.getData(ACCOUNT);
            memory.deleteAccount(account);
        } else if (e.is(TRANSACTION)) {
            memory.saveTransaction(e.getTransaction());
        } else if (e.is(TRANSACTIONS)) {
            memory.saveTransactions(e.getTransactions());
        }
    }
}
