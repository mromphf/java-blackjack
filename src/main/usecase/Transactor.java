package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.*;

import static main.usecase.Layout.BET;
import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Transactor implements NavListener, GameStateListener, ActionListener {

    private final List<Transaction> transactions;
    private final Collection<BalanceListener> balanceListeners;
    private final Collection<TransactionListener> transactionListeners;
    private Account account;

    public Transactor() {
        this.account = new Account(UUID.randomUUID(), "Placeholder", 0, LocalDateTime.now());
        this.balanceListeners = new LinkedList<>();
        this.transactions = new LinkedList<>();
        this.transactionListeners = new LinkedList<>();
    }

    public void registerBalanceListener(BalanceListener balanceListener) {
        balanceListeners.add(balanceListener);
    }

    public void registerTransactionListener(TransactionListener transactionListener) {
        transactionListeners.add(transactionListener);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();
        final List<Transaction> workingTransactions = new LinkedList<>();

        if (actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE)) {
            workingTransactions.add(new Transaction(
                    LocalDateTime.now(), account.getKey(), BUY_INSURANCE.name(), snapshot.getBet() * -1));
        }

        if (actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE) || a.equals(SPLIT))) {
            workingTransactions.add(new Transaction(
                    LocalDateTime.now(), account.getKey(), "DOUBLE OR SPLIT", snapshot.getBet() * -1));
        }

        if (snapshot.isResolved()) {
            workingTransactions.add(new Transaction(
                    LocalDateTime.now(), account.getKey(), snapshot.getOutcome().name(), settleBet(snapshot)));
        }

        if (workingTransactions.size() > 0) {
            transactions.addAll(workingTransactions);
            transactionListeners.forEach(l -> l.onTransactions(workingTransactions));
            balanceListeners.forEach(l -> l.onBalanceUpdated(account.updateBalance(transactions)));
        }
    }

    @Override
    public void onBetPlaced(int amount) {
        Transaction trans_bet = new Transaction(
                LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
        transactions.add(trans_bet);
        transactionListeners.forEach(l -> l.onTransaction(trans_bet));
        balanceListeners.forEach(l -> l.onBalanceUpdated(account.updateBalance(transactions)));
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        if (layout == BET) {
            this.account = account;
            balanceListeners.forEach(l -> l.onBalanceUpdated(account));
        }
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public void onActionTaken(Action action) {}
}
