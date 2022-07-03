package main.domain.function;

import main.domain.Assessment;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.function.CardFunctions.settleBet;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.RoundPredicate.outcomeIsResolved;

public class OutcomeAssessment implements Assessment {

    public static OutcomeAssessment outcomeAssessment() {
        return new OutcomeAssessment();
    }

    @Override
    public Optional<Transaction> apply(Snapshot snapshot) {
        if (outcomeIsResolved.test(snapshot)) {
            return Optional.of(transaction(
                    snapshot.getTimestamp(),
                    snapshot.getAccountKey(),
                    snapshot.getOutcome().name(),
                    settleBet(snapshot, snapshot.getOutcome())));
        } else {
            return empty();
        }
    }
}
