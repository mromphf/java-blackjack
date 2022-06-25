package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.EVALUATORS;
import static main.domain.model.Transaction.transaction;

public class Transactor extends EventConnection implements SnapshotListener, AccountListener {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    @Inject
    public Transactor(@Named(EVALUATORS) Collection<Function<Snapshot, Optional<Transaction>>> evaluators) {
        this.evaluationFunctions = evaluators;
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
        final String description = "SIGNING BONUS";
        final int signingBonus = 200;
        final Transaction transaction = transaction(now(), account.getKey(), description, signingBonus);

        eventNetwork.onTransactionIssued(transaction);
    }
}
