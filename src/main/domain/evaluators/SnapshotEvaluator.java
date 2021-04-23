package main.domain.evaluators;

import main.domain.Snapshot;
import main.domain.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface SnapshotEvaluator {
    Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot);
}
