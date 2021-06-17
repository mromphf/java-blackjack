package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import main.usecase.eventing.*;
import main.usecase.eventing.EventListener;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class Transactor extends EventConnection implements GameStateListener, EventListener, BetListener {

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
            final int signingBonus = 200;
            final UUID accountKey = eventNetwork.fulfill(ACCOUNT_SELECTED).getKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Message m = Message.of(TRANSACTION, transaction);

            eventNetwork.onEvent(m);
        }
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        if (event.is(BET_PLACED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final Bet bet = event.getData();
            final String description = "BET";
            final int signingBonus = (bet.getVal() * -1);
            final UUID accountKey = bet.getAccountKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Message m = Message.of(TRANSACTION, transaction);

            eventNetwork.onEvent(m);
        }
    }
}
