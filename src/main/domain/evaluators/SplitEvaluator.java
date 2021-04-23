package main.domain.evaluators;

import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import static main.domain.Action.SPLIT;

public class SplitEvaluator implements SnapshotEvaluator {

    @Override
    public Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();

        if (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT))) {
            return Optional.of(
                    new Transaction(LocalDateTime.now(), accountKey, "SPLIT", snapshot.getNegativeBet())
            );
        } else {
            return Optional.empty();
        }
    }
}
