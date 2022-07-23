package main.domain.function;

import main.domain.model.TableView;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.LowOrderPredicate.startOfRound;

public class BetAssessment implements Assessment {

    public static BetAssessment betAssessment() {
        return new BetAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (startOfRound.test(tableView)) {
            return Optional.of(transaction(
                    tableView.timestamp(),
                    tableView.playerAccountKey(),
                    "BET",
                    tableView.negativeBet()));
        } else {
            return empty();
        }
    }
}
