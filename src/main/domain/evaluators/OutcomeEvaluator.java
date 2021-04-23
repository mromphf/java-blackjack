package main.domain.evaluators;

import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static main.domain.Rules.settleBet;

public class OutcomeEvaluator implements SnapshotEvaluator {

    @Override
    public Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot) {
        if (snapshot.isResolved()) {
            return Optional.of(new Transaction(
                    LocalDateTime.now(), accountKey, snapshot.getOutcome().name(), settleBet(snapshot))
            );
        } else {
            return Optional.empty();
        }
    }
}
