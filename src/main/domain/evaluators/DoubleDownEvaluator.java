package main.domain.evaluators;

import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import static main.domain.Action.DOUBLE;

public class DoubleDownEvaluator implements TransactionEvaluator {

    @Override
    public Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();

        if (actionsTaken.stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
            return Optional.of(
                    new Transaction(LocalDateTime.now(), accountKey, "DOUBLE", snapshot.getNegativeBet())
            );
        } else {
            return Optional.empty();
        }
    }
}
