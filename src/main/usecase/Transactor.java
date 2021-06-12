package main.usecase;

import main.domain.*;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.usecase.Predicate.*;

public class Transactor extends EventConnection implements GameStateListener, EventListener {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    public Transactor(Collection<Function<Snapshot, Optional<Transaction>>> evaluators) {
        this.evaluationFunctions = evaluators;
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        final List<Transaction> workingTransactions = evaluationFunctions.stream()
            .map(f -> f.apply(snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        final Message message = Message.of(TRANSACTION_SERIES, workingTransactions);

        eventNetwork.onEvent(message);
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(ACCOUNT_CREATED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final String description = "SIGNING BONUS";
            final UUID accountKey = message.getAccount().getKey();
            final int signingBonus = 200;
            final Transaction t = new Transaction(timestamp, accountKey, description, signingBonus);
            final Message m = Message.of(TRANSACTION, t);

            eventNetwork.onEvent(m);
        } else if (message.is(BET_PLACED)) {
            final Transaction t = new Transaction(LocalDateTime.now(), message.getAccount().getKey(), "BET", (message.getAmount() * -1));
            final Message m = Message.of(TRANSACTION, t);

            eventNetwork.onEvent(m);
        }
    }
}
