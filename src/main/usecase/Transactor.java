package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;

import java.util.*;

import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Transactor implements NavListener, GameStateListener {

    private final Collection<TransactionListener> transactionListeners;
    private Account account;

    public Transactor() {
        this.account = new Account(UUID.randomUUID(), "Placeholder", 0);
        this.transactionListeners = new LinkedList<>();
    }

    public void registerSettlementListener(TransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();

        if (actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE)) {
            account.updateBalance(snapshot.getBet() * -1);
        }

        if (actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE) || a.equals(SPLIT))) {
            account.updateBalance(snapshot.getBet() * -1);
        }

        if (snapshot.isResolved()) {
            account.updateBalance(settleBet(snapshot));
        }

        transactionListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
    }

    @Override
    public void onStartNewRound(int bet) {
        account.updateBalance(bet * -1);
        transactionListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
    }

    @Override
    public void onMoveToBettingTable(Account account) {
        this.account = account;
        transactionListeners.forEach(l -> l.onBalanceChanged(this.account.getBalance()));
    }

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onStopPlaying() {}
}
