package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Rules.compileTransactions;
import static main.usecase.Predicate.*;

public class Transactor extends EventConnection implements EventListener {

    private final List<Transaction> transactions;
    private Account account;

    public Transactor() {
        this.account = new Account(UUID.randomUUID(), "Placeholder", 0, LocalDateTime.now());
        this.transactions = new LinkedList<>();
    }

    @Override
    public void listen(Event e) {
        if (e.is(ACCOUNT_SELECTED)) {
            account = e.getAccount();
            eventNetwork.post(new Event(BALANCE_UPDATED, account));
        } else if (e.is(GAME_STATE_CHANGED)) {
            Collection<Transaction> workingTransactions = compileTransactions(account.getKey(), e.getSnapshot());
            transactions.addAll(workingTransactions);
            eventNetwork.post(new Event(TRANSACTIONS, new ArrayList<>(workingTransactions)));
            eventNetwork.post(new Event(BALANCE_UPDATED, account.updateBalance(transactions)));
        } else if (e.is(BET_PLACED)) {
            final int amount = e.getInt();
            Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
            transactions.add(t);
            eventNetwork.post(new Event(TRANSACTION, t));
            eventNetwork.post(new Event(BALANCE_UPDATED, account.updateBalance(transactions)));
        }
    }
}
