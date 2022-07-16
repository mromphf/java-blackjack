package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.LowOrderPredicate.playerPurchasedInsurance;

public class InsuranceAssessment implements Assessment {

    public static InsuranceAssessment insuranceAssessment() {
        return new InsuranceAssessment();
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        if (playerPurchasedInsurance.test(snapshot)) {
            return Optional.of(transaction(
                    snapshot.getTimestamp(),
                    snapshot.getAccountKey(),
                    BUY_INSURANCE.name(),
                    snapshot.getNegativeBet()));
        } else {
            return empty();
        }
    }
}
