package main.domain.evaluators;

import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static main.domain.Action.BUY_INSURANCE;

public class InsuranceEvaluator implements SnapshotEvaluator {

    @Override
    public Optional<Transaction> evaluate(UUID accountKey, Snapshot snapshot) {
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final boolean insurancePurchased = actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE);

        if (insurancePurchased) {
            return Optional.of(new Transaction(LocalDateTime.now(), accountKey, BUY_INSURANCE.name(), snapshot.getNegativeBet()));
        } else {
            return Optional.empty();
        }
    }
}
