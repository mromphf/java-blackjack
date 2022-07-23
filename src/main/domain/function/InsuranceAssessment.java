package main.domain.function;

import main.domain.model.TableView;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.LowOrderPredicate.insurancePurchased;

public class InsuranceAssessment implements Assessment {

    public static InsuranceAssessment insuranceAssessment() {
        return new InsuranceAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (insurancePurchased.test(tableView)) {
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
