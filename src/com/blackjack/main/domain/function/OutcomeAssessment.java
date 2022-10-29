package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import java.util.Optional;

import static com.blackjack.main.domain.function.Settlement.settleBet;
import static com.blackjack.main.domain.model.Transaction.transaction;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.*;
import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;

public class OutcomeAssessment implements Assessment {

    public static OutcomeAssessment outcomeAssessment() {
        return new OutcomeAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (outcomeIsResolved.test(tableView)) {
            return Optional.of(transaction(now(),
                    tableView.playerAccountKey(),
                    tableView.outcome().name(),
                    settleBet(tableView)));
        } else {
            return empty();
        }
    }
}
