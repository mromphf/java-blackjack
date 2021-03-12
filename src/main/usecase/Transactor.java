package main.usecase;

import main.domain.*;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Rules.compileTransactions;
import static main.usecase.Layout.BET;

public class Transactor extends EventConnection implements NavListener, GameStateListener, ActionListener {

    private final List<Transaction> transactions;
    private Account account;

    public Transactor() {
        this.account = new Account(UUID.randomUUID(), "Placeholder", LocalDateTime.now());
        this.transactions = new LinkedList<>();
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        Collection<Transaction> workingTransactions = compileTransactions(account.getKey(), snapshot);
        transactions.addAll(workingTransactions);
        eventNetwork.onTransactions(new ArrayList<>(workingTransactions));
        eventNetwork.onBalanceUpdated(account.updateBalance(transactions));
    }

    @Override
    public void onBetPlaced(int amount) {
        Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
        transactions.add(t);
        eventNetwork.onTransaction(t);
        eventNetwork.onBalanceUpdated(account.updateBalance(transactions));
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        if (layout == BET) {
            this.account = account;
            eventNetwork.onBalanceUpdated(account);
        }
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public void onActionTaken(Action action) {}
}
