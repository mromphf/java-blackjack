package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static com.blackjack.main.domain.model.Action.DOUBLE;
import static com.blackjack.main.domain.model.Transaction.transaction;

public class DoubleDownAssessment implements Assessment {

    public static DoubleDownAssessment doubleDownAssessment() {
        return new DoubleDownAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (tableView.actionsTaken().stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
            return Optional.of(transaction(
                    tableView.timestamp(), tableView.playerAccountKey(),
                    DOUBLE.name(), tableView.negativeBet()));
        } else {
            return empty();
        }
    }
}
