package main.usecase;

import main.domain.*;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Transactor extends EventConnection implements GameStateListener, ActionListener, AccountListener {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    public Transactor(Collection<Function<Snapshot, Optional<Transaction>>> evaluators) {
        this.evaluationFunctions = evaluators;
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final Collection<Transaction> workingTransactions = evaluationFunctions.stream()
            .map(f -> f.apply(snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        eventNetwork.onTransactions(new ArrayList<>(workingTransactions));
    }

    @Override
    public void onBetPlaced(Account account, int amount) {
        final Transaction t = new Transaction(LocalDateTime.now(), account.getKey(), "BET", (amount * -1));
        eventNetwork.onTransaction(t);
    }

    @Override
    public void onNewAccountOpened(Account account) {
        final LocalDateTime timestamp = LocalDateTime.now();
        final String description = "SIGNING BONUS";
        final UUID accountKey = account.getKey();
        final int signingBonus = 200;
        final Transaction t = new Transaction(timestamp, accountKey, description, signingBonus);

        eventNetwork.onTransaction(t);
    }

    @Override
    public void onActionTaken(Action action) {}

    @Override
    public void onAccountDeleted(Account account) {}
}
