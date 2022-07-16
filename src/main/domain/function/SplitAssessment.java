package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Action;
import main.domain.model.TableView;
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
    public Optional<Transaction> apply(TableView tableView) {
        final Collection<Action> actionsTaken = tableView.actionsTaken();

        final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                actionsTaken.stream().noneMatch(a ->
                        a.equals(HIT) || a.equals(DOUBLE) || a.equals(STAND)));

        if (chargeForSplit) {
            return Optional.of(transaction(
                    tableView.timestamp(),
                    tableView.playerAccountKey(),
                    SPLIT.name(),
                    tableView.negativeBet()));
        } else {
            return empty();
        }
    }

}
