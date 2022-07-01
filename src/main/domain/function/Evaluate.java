package main.domain.function;

import main.domain.model.Action;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static main.domain.function.RoundPredicate.*;
import static main.domain.model.Action.*;
import static main.domain.function.Rules.settleBet;
import static main.domain.model.Transaction.transaction;

public class Evaluate {

    public static Collection<Function<Snapshot, Optional<Transaction>>> transactionEvaluators() {
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = new HashSet<>();

        evaluators.add(Evaluate.doubleDownTransactions());
        evaluators.add(Evaluate.insuranceTransactions());
        evaluators.add(Evaluate.outcomeTransactions());
        evaluators.add(Evaluate.splitTransactions());
        evaluators.add(Evaluate.betTransactions());

        return evaluators;
    }

    private static Function<Snapshot, Optional<Transaction>> betTransactions() {
        return (snapshot) -> {
            if (startOfRound.test(snapshot)) {
                return Optional.of(transaction(
                        snapshot.getTimestamp(),
                        snapshot.getAccountKey(),
                        "BET",
                        snapshot.getNegativeBet()));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> insuranceTransactions() {
        return (snapshot) -> {
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
        };
    }

    private static Function<Snapshot, Optional<Transaction>> doubleDownTransactions() {
        return (snapshot) -> {
            if (snapshot.getActionsTaken().stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
                return Optional.of(transaction(
                        snapshot.getTimestamp(),
                        snapshot.getAccountKey(),
                        DOUBLE.name(),
                        snapshot.getNegativeBet()));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> outcomeTransactions() {
        return (snapshot) -> {
            if (outcomeIsResolved.test(snapshot)) {
                return Optional.of(transaction(
                        snapshot.getTimestamp(),
                        snapshot.getAccountKey(),
                        snapshot.getOutcome().name(),
                        settleBet(snapshot)));
            } else {
                return empty();
            }
        };
    }

    private static Function<Snapshot, Optional<Transaction>> splitTransactions() {
        return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();

            final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                    actionsTaken.stream().noneMatch(a ->
                            a.equals(HIT) || a.equals(DOUBLE) || a.equals(STAND)));

            if (chargeForSplit) {
                return Optional.of(transaction(
                        snapshot.getTimestamp(),
                        snapshot.getAccountKey(),
                        SPLIT.name(),
                        snapshot.getNegativeBet()));
            } else {
                return empty();
            }
        };
    }
}
