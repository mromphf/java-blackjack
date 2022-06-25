package main.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static java.util.Optional.empty;
import static main.domain.Action.*;
import static main.domain.Rules.settleBet;
import static main.domain.Snapshot.outcomeIsResolved;
import static main.domain.Transaction.transaction;

public class Evaluate {

    public static Collection<Function<Snapshot, Optional<Transaction>>> transactionEvaluators() {
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = new HashSet<>();

        evaluators.add(Evaluate.doubleDownTransactions());
        evaluators.add(Evaluate.insuranceTransactions());
        evaluators.add(Evaluate.outcomeTransactions());
        evaluators.add(Evaluate.splitTransactions());

        return evaluators;
    }

    private static Function<Snapshot, Optional<Transaction>> insuranceTransactions() {
       return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();
            final boolean insurancePurchased = actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE);
            final LocalDateTime timestamp = snapshot.getTimestamp();
            final UUID accountKey = snapshot.getAccountKey();
            final String description = BUY_INSURANCE.name();
            final int bet = snapshot.getNegativeBet();

           if (insurancePurchased) {
                return Optional.of(transaction(timestamp, accountKey, description, bet));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> doubleDownTransactions() {
        return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();

            if (actionsTaken.stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
                final LocalDateTime timestamp = snapshot.getTimestamp();
                final UUID accountKey = snapshot.getAccountKey();
                final String description = DOUBLE.name();
                final int bet = snapshot.getNegativeBet();

                return Optional.of(transaction(timestamp, accountKey, description, bet));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> outcomeTransactions() {
        return (snapshot) -> {
            if (outcomeIsResolved.test(snapshot)) {
                final LocalDateTime timestamp = snapshot.getTimestamp();
                final UUID accountKey = snapshot.getAccountKey();
                final String description = snapshot.getOutcome().name();
                final int payout = settleBet(snapshot);

                return Optional.of(transaction(timestamp, accountKey, description, payout));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> splitTransactions() {
        return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();
            final LocalDateTime timestamp = snapshot.getTimestamp();
            final UUID accountKey = snapshot.getAccountKey();
            final String description = SPLIT.name();
            final int bet = snapshot.getNegativeBet();

            final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                    actionsTaken.stream().noneMatch(a ->
                            a.equals(HIT) ||
                            a.equals(DOUBLE) ||
                            a.equals(STAND)));

            final Transaction transaction = transaction(timestamp, accountKey, description, bet);

            if (chargeForSplit) {
                return Optional.of(transaction);
            } else {
                return empty();
            }
        };
    }
}
