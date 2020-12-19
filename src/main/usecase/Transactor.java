package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Transactor implements NavListener, GameStateListener {

    private final Set<Transaction> transactions;
    private final Collection<TransactionListener> transactionListeners;
    private Account account;

    public Transactor() {
        this.account = new Account(UUID.randomUUID(), "Placeholder", 0, LocalDateTime.now());
        this.transactionListeners = new LinkedList<>();
        this.transactions = new HashSet<>();
    }

    public void registerSettlementListener(TransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();

        if (actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE)) {
            Transaction trans_buyInsurance = new Transaction(
                    LocalDateTime.now(), account.getKey(), BUY_INSURANCE.name(), snapshot.getBet() * -1);
            transactions.add(trans_buyInsurance);
            //account = account.updateBalance(snapshot.getBet() * -1);
        }

        if (actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE) || a.equals(SPLIT))) {
            Transaction trans_doubleOrSplit = new Transaction(
                    LocalDateTime.now(), account.getKey(), "Double or Split", snapshot.getBet() * -1);
            transactions.add(trans_doubleOrSplit);
            //account = account.updateBalance(snapshot.getBet() * -1);
        }

        if (snapshot.isResolved()) {
            Transaction trans_doubleOrSplit = new Transaction(
                    LocalDateTime.now(), account.getKey(), snapshot.getOutcome().name(), settleBet(snapshot));
            transactions.add(trans_doubleOrSplit);
            //account = account.updateBalance(settleBet(snapshot));
        }

        transactionListeners.forEach(l -> l.onBalanceChanged(account.updateBalance(account.deriveBalance(transactions))));
    }

    @Override
    public void onStartNewRound(int bet) {
        Transaction trans_bet = new Transaction(
                LocalDateTime.now(), account.getKey(), "Bet", (bet * -1));
        transactions.add(trans_bet);
        //account = account.updateBalance(bet * -1);
        transactionListeners.forEach(l -> l.onBalanceChanged(account.updateBalance(account.deriveBalance(transactions))));
    }

    @Override
    public void onMoveToBettingTable(Account account) {
        this.account = account;
        transactionListeners.forEach(l -> l.onBalanceChanged(account));
    }

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onStopPlaying() {}
}
