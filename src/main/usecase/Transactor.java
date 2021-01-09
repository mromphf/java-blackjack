package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Rules.compileTransactions;
import static main.usecase.DataKey.*;
import static main.usecase.Event.*;
import static main.usecase.Layout.BET;
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
        if (e.is(LAYOUT_CHANGED) && e.getData(LAYOUT).equals(BET) && e.hasData(ACCOUNT)) {
            this.account = (Account) e.getData(ACCOUNT);
            eventNetwork.post(balanceUpdated(account));
        } else if (e.is(GAME_STATE_CHANGED)) {
            final Snapshot snapshot = (Snapshot) e.getData(SNAPSHOT);
            Collection<Transaction> workingTransactions = compileTransactions(account.getKey(), snapshot);
            transactions.addAll(workingTransactions);
            eventNetwork.post(onTransactions(new ArrayList<>(workingTransactions)));
            eventNetwork.post(balanceUpdated(account.updateBalance(transactions)));
        } else if (e.is(BET_PLACED)) {
            final int amount = (int) e.getData(INT);
            Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
            transactions.add(t);
            eventNetwork.post(onTransaction(t));
            eventNetwork.post(balanceUpdated(account.updateBalance(transactions)));
        }
    }
}
