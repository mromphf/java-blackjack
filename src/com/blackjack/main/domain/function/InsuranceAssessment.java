package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static com.blackjack.main.domain.model.Action.BUY_INSURANCE;
import static com.blackjack.main.domain.model.Transaction.transaction;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.chargeForInsurance;

public class InsuranceAssessment implements Assessment {

    public static InsuranceAssessment insuranceAssessment() {
        return new InsuranceAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (chargeForInsurance.test(tableView)) {
            return Optional.of(transaction(
                    tableView.timestamp(),
                    tableView.playerAccountKey(),
                    BUY_INSURANCE.name(),
                    tableView.negativeBet()));
        } else {
            return empty();
        }
    }
}
