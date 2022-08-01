package main.domain.function;

import main.domain.model.Table;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static main.domain.model.Action.DOUBLE;
import static main.domain.model.Transaction.transaction;

public class DoubleDownAssessment implements Assessment {

    public static DoubleDownAssessment doubleDownAssessment() {
        return new DoubleDownAssessment();
    }

    @Override
    public Optional<Transaction> apply(Table table) {
        if (table.actionsTaken().stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
            return Optional.of(transaction(
                    now(), table.playerAccountKey(),
                    DOUBLE.name(), table.negativeBet()));
        } else {
            return empty();
        }
    }
}
