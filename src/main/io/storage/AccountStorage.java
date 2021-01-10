package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.Event;
import main.usecase.EventListener;

import java.time.LocalDateTime;

import static main.usecase.Predicate.*;

public class AccountStorage extends EventConnection implements EventListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        eventNetwork.post(new Event(ACCOUNTS_LOADED, memory.loadAllAccounts()));
    }

    public void loadAllTransactions() {
        eventNetwork.post(new Event(TRANSACTIONS_LOADED, memory.loadAllTransactions()));
    }

    @Override
    public void listen(Event e) {
        if (e.is(ACCOUNT_OPENED)) {
            final Account account = e.getAccount();
            final Transaction t = new Transaction(
                    LocalDateTime.now(),
                    account.getKey(),
                    "SIGNING BONUS",
                    200
            );

            memory.saveNewAccount(account);
            memory.saveTransaction(t);
        } else if (e.is(ACCOUNT_DELETED)) {
            memory.deleteAccount(e.getAccount());
        } else if (e.is(TRANSACTION)) {
            memory.saveTransaction(e.getTransaction());
        } else if (e.is(TRANSACTION_BATCH)) {
            memory.saveTransactions(e.getTransactions());
        }
    }
}
