package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Action;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Transaction.transaction;

public class InsuranceAssessment implements Assessment {

    public static InsuranceAssessment insuranceAssessment() {
        return new InsuranceAssessment();
    }

    @Override
    public Action action() {
        return BUY_INSURANCE;
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final boolean insurancePurchased = actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE);

        if (insurancePurchased) {
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
