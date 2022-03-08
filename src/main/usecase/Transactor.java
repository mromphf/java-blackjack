package main.usecase;

import com.google.inject.Inject;
import main.domain.Account;
import main.domain.Bet;
import main.domain.Snapshot;
import main.domain.Transaction;
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

import static java.time.LocalDateTime.now;
import static main.domain.Evaluate.betTransaction;

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
            eventNetwork.onTransactionSeriesIssued(workingTransactions);
        }
    }

    @Override
    public void onAccountCreated(Account account) {
        final UUID accountKey = account.getKey();
        final LocalDateTime timestamp = now();
        final String description = "SIGNING BONUS";
        final int signingBonus = 200;
        final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);

        eventNetwork.onTransactionIssued(transaction);
    }

    @Override
    public void onAccountDeleted(Account account) {
        // No-op stub
    }

    @Override
    public void onAccountSelected(Account account) {
        // No-op stub
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        // No-op stub
    }

    public void onBetEvent(Bet bet) {
        eventNetwork.onTransactionIssued(betTransaction(now(), bet));
    }
}
