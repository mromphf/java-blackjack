package main.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;

import static main.domain.Action.BUY_INSURANCE;
import static main.domain.Action.DOUBLE;
import static main.domain.Action.HIT;
import static main.domain.Action.REFILL;
import static main.domain.Action.SPLIT;
import static main.domain.Rules.settleBet;

public class Evaluate {

    public static Function<Snapshot, Optional<Transaction>> insuranceTransactions() {
       return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();
            final boolean insurancePurchased = actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE);
            final Transaction transaction = new Transaction(
                    LocalDateTime.now(), snapshot.getAccountKey(), BUY_INSURANCE.name(), snapshot.getNegativeBet());

            if (insurancePurchased) {
                return Optional.of(transaction);
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> doubleDownTransactions() {
        return (snapshot) -> {
            final Stack<Action> actionsTaken = snapshot.getActionsTaken();

            if (actionsTaken.stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
                return Optional.of(
                        new Transaction(LocalDateTime.now(), snapshot.getAccountKey(), "DOUBLE", snapshot.getNegativeBet())
                );
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> outcomeTransactions() {
        return (snapshot) -> {
            if (snapshot.isHandResolved()) {
                return Optional.of(new Transaction(
                        LocalDateTime.now(), snapshot.getAccountKey(), snapshot.getOutcome().name(), settleBet(snapshot))
                );
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> splitTransactions() {
        return (snapshot) -> {
            final Stack<Action> actionsTaken = snapshot.getActionsTaken();
            final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                    actionsTaken.stream().noneMatch(a -> a.equals(HIT)));
            final Transaction transaction = new Transaction(
                    LocalDateTime.now(), snapshot.getAccountKey(), SPLIT.name(), snapshot.getNegativeBet());

            if (chargeForSplit) {
                return Optional.of(transaction);
            } else {
                return Optional.empty();
            }
        };
    }
}
