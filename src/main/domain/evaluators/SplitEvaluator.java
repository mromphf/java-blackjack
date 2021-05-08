package main.domain.evaluators;

import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import static main.domain.Action.HIT;
import static main.domain.Action.SPLIT;

public class SplitEvaluator implements TransactionEvaluator {

    @Override
    public Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot) {
        final Stack<Action> actionsTaken = snapshot.getActionsTaken();
        final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                actionsTaken.stream().noneMatch(a -> a.equals(HIT)));
        final Transaction transaction = new Transaction(
                LocalDateTime.now(), accountKey, SPLIT.name(), snapshot.getNegativeBet());

        if (chargeForSplit) {
            return Optional.of(transaction);
        } else {
            return Optional.empty();
        }
    }
}
