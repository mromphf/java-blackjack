package main.domain.function;

import main.domain.model.Table;
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
    public Optional<Transaction> apply(Table table) {
        if (startOfRound.test(table)) {
            return Optional.of(transaction(
                    table.timestamp(),
                    table.playerAccountKey(),
                    "BET",
                    table.negativeBet()));
        } else {
            return empty();
        }
    }
}
