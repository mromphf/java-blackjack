package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Action;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Action.DOUBLE;
import static main.domain.model.Transaction.transaction;

public class DoubleDownAssessment implements Assessment {

    public static DoubleDownAssessment doubleDownAssessment() {
        return new DoubleDownAssessment();
    }

    @Override
    public Action action() {
        return DOUBLE;
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        if (snapshot.getActionsTaken().stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
            return Optional.of(transaction(
                    snapshot.getTimestamp(),
                    snapshot.getAccountKey(),
                    DOUBLE.name(),
                    snapshot.getNegativeBet()));
        } else {
            return empty();
        }
    }
}
