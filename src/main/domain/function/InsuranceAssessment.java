package main.domain.function;

import main.domain.model.Table;
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
    public Optional<Transaction> apply(Table table) {
        if (insurancePurchased.test(table)) {
            return Optional.of(transaction(
                    table.timestamp(),
                    table.playerAccountKey(),
                    BUY_INSURANCE.name(),
                    table.negativeBet()));
        } else {
            return empty();
        }
    }
}
