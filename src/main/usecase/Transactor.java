package main.usecase;

import main.domain.*;
import main.domain.evaluators.TransactionEvaluator;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Transactor extends EventConnection implements GameStateListener, ActionListener, AccountListener, NavListener {

    private final Collection<TransactionEvaluator> evaluators;
    private final List<Transaction> transactions;
    private Account account;

    public Transactor(Collection<TransactionEvaluator> evaluators) {
        this.evaluators = evaluators;
        this.transactions = new LinkedList<>();
        this.account = Account.placeholder();
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Collection<Transaction> workingTransactions = evaluators.stream()
            .map(e -> e.evaluate(account.getKey(), snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        transactions.addAll(workingTransactions);
        eventNetwork.onTransactions(new ArrayList<>(workingTransactions));
        eventNetwork.onBalanceUpdated(account.updateBalance(transactions));
    }

    @Override
    public void onBetPlaced(Account account, int amount) {
        final Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
        transactions.add(t);
        eventNetwork.onTransaction(t);
        eventNetwork.onBalanceUpdated(account.updateBalance(transactions));
    }

    @Override
    public void onNewAccountOpened(Account account) {
        final LocalDateTime timestamp = LocalDateTime.now();
        final String description = "SIGNING BONUS";
        final UUID accountKey = account.getKey();
        final int signingBonus = 200;
        final Transaction t = new Transaction(timestamp, accountKey, description, signingBonus);

        eventNetwork.onTransaction(t);
        eventNetwork.onBalanceUpdated(account.updateBalance(t));
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        this.account = account;
    }

    @Override
    public void onActionTaken(Action action) {}

    @Override
    public void onAccountDeleted(Account account) {}

    @Override
    public void onChangeLayout(Layout layout) {}
}
