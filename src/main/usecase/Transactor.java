package main.usecase;

import main.domain.*;
import main.domain.evaluators.SnapshotEvaluator;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Transactor extends EventConnection implements GameStateListener, ActionListener {

    private final Collection<SnapshotEvaluator> evaluators;
    private final List<Transaction> transactions;

    public Transactor(Collection<SnapshotEvaluator> evaluators) {
        this.evaluators = evaluators;
        this.transactions = new LinkedList<>();
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Account account = snapshot.getAccount();
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
    public void onActionTaken(Action action) {}
}
