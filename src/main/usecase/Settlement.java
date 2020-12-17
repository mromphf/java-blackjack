package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;

import java.util.*;

import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Settlement implements NavListener, GameStateListener {

    private final Queue<Account> accounts;
    private final Collection<SettlementListener> settlementListeners;

    public Settlement() {
        this.accounts = new ArrayDeque<>();
        this.settlementListeners = new LinkedList<>();
    }

    public void registerSettlementListener(SettlementListener settlementListener) {
        settlementListeners.add(settlementListener);

        if (!accounts.isEmpty()) {
            int firstBalance = accounts.peek().getBalance();
            settlementListeners.forEach(l -> l.onBalanceChanged(firstBalance));
        }
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();

        if (actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE)) {
            accounts.forEach(a -> a.updateBalance(snapshot.getBet() * -1));
        }

        if (actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE) || a.equals(SPLIT))) {
            accounts.forEach(a -> a.updateBalance(snapshot.getBet() * -1));
        }

        if (snapshot.isResolved()) {
            accounts.forEach(a -> a.updateBalance(settleBet(snapshot)));
        }

        if (!accounts.isEmpty()) {
            int firstBalance = accounts.peek().getBalance();
            settlementListeners.forEach(l -> l.onBalanceChanged(firstBalance));
        }
    }

    @Override
    public void onStartNewRound(int bet) {
        accounts.forEach(a -> a.updateBalance(bet * -1));

        if (!accounts.isEmpty()) {
            int firstBalance = accounts.peek().getBalance();
            settlementListeners.forEach(l -> l.onBalanceChanged(firstBalance));
        }
    }

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onMoveToBettingTable(Account account) {
        accounts.add(account);
        if (!accounts.isEmpty()) {
            int firstBalance = accounts.peek().getBalance();
            settlementListeners.forEach(l -> l.onBalanceChanged(firstBalance));
        }
    }

    @Override
    public void onStopPlaying() {
        accounts.clear();
    }
}
