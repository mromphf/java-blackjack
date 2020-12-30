package main.usecase;

import main.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Rules.compileTransactions;
import static main.usecase.Layout.BET;

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
        Collection<Transaction> workingTransactions = compileTransactions(account.getKey(), snapshot);
        transactions.addAll(workingTransactions);
        transactionListeners.forEach(l -> l.onTransactions(new ArrayList<>(workingTransactions)));
        balanceListeners.forEach(l -> l.onBalanceUpdated(account.updateBalance(transactions)));
    }

    @Override
    public void onBetPlaced(int amount) {
        Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
        transactions.add(t);
        transactionListeners.forEach(l -> l.onTransaction(t));
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
