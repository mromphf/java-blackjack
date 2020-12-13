package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Settlement implements NavListener, GameStateListener {

    private final Account account;
    private final Collection<SettlementListener> settlementListeners;

    public Settlement(Account account) {
        this.account = account;
        this.settlementListeners = new LinkedList<>();
    }

    public void registerSettlementListener(SettlementListener settlementListener) {
        settlementListeners.add(settlementListener);
        settlementListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
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

        settlementListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
    }

    @Override
    public void onStartNewRound(int bet) {
        account.updateBalance(bet * -1);
        settlementListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
    }

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onStopPlaying() {}
}
