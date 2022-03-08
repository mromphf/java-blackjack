package main.usecase;

import com.google.inject.Inject;
import main.domain.*;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class Transactor extends EventConnection implements SnapshotListener, AccountListener {

    private final UUID key;
    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    @Inject
    public Transactor(UUID key, Collection<Function<Snapshot, Optional<Transaction>>> evaluators) {
        this.key = key;
        this.evaluationFunctions = evaluators;
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        final Collection<Transaction> workingTransactions = evaluationFunctions.stream()
            .map(f -> f.apply(snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        if (workingTransactions.size() > 0) {
            final LocalDateTime timestamp = snapshot.getTimestamp();

            final Event<Collection<Transaction>> event = new Event<>(
                    key, timestamp, TRANSACTION_SERIES_ISSUED, workingTransactions
            );

            eventNetwork.onTransactionsEvent(event);
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            final UUID accountKey = event.getData().getKey();
            final LocalDateTime timestamp = LocalDateTime.now();
            final String description = "SIGNING BONUS";
            final int signingBonus = 200;

            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Event<Transaction> evt = new Event<>(key, timestamp, TRANSACTION_ISSUED, transaction);

            eventNetwork.onTransactionEvent(evt);
        }
    }

    public void onBetEvent(Bet bet) {
        final LocalDateTime timestamp = LocalDateTime.now();
        final Transaction transaction = Evaluate.betTransaction(timestamp, bet);
        final Event<Transaction> evt = new Event<>(key, timestamp, TRANSACTION_ISSUED, transaction);

        eventNetwork.onTransactionEvent(evt);
    }
}
