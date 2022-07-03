package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Action;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Action.*;
import static main.domain.model.Transaction.transaction;

public class SplitAssessment implements Assessment {

    public static SplitAssessment splitAssessment() {
        return new SplitAssessment();
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();

        final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                actionsTaken.stream().noneMatch(a ->
                        a.equals(HIT) || a.equals(DOUBLE) || a.equals(STAND)));

        if (chargeForSplit) {
            return Optional.of(transaction(
                    snapshot.getTimestamp(),
                    snapshot.getAccountKey(),
                    SPLIT.name(),
                    snapshot.getNegativeBet()));
        } else {
            return empty();
        }
    }

}
