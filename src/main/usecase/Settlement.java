package main.usecase;

import main.domain.Account;
import main.domain.Snapshot;

import java.util.Collection;
import java.util.LinkedList;

import static main.domain.Rules.settleBet;

public class Settlement implements GameStateListener {

    private final Account account;
    private final Collection<SettlementListener> settlementListeners;

    public Settlement(Account account) {
        this.account = account;
        this.settlementListeners = new LinkedList<>();
    }

    public void registerSettlementListener(SettlementListener settlementListener) {
        settlementListeners.add(settlementListener);
    }

    @Override
    public void onUpdate(int balance, Snapshot snapshot) {
        if (snapshot.isResolved()) {
            account.updateBalance(settleBet(snapshot));
        }
        settlementListeners.forEach(l -> l.onBalanceChanged(account.getBalance()));
    }
}
