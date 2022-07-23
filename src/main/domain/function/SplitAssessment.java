package main.domain.function;

import main.domain.model.Action;
import main.domain.model.Table;
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
    public Optional<Transaction> apply(Table table) {
        final Collection<Action> actionsTaken = table.actionsTaken();

        final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                actionsTaken.stream().noneMatch(a ->
                        a.equals(HIT) || a.equals(DOUBLE) || a.equals(STAND)));

        if (chargeForSplit) {
            return Optional.of(transaction(
                    table.timestamp(),
                    table.playerAccountKey(),
                    SPLIT.name(),
                    table.negativeBet()));
        } else {
            return empty();
        }
    }

}
