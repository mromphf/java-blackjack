package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.RoundPredicate.startOfRound;

public class BetAssessment implements Assessment {

    public static BetAssessment betAssessment() {
        return new BetAssessment();
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        if (startOfRound.test(snapshot)) {
            return Optional.of(transaction(
                    snapshot.getTimestamp(),
                    snapshot.getAccountKey(),
                    "BET",
                    snapshot.getNegativeBet()));
        } else {
            return empty();
        }
    }
}
