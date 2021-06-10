package main.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static main.domain.Action.*;
import static main.domain.Rules.settleBet;

public class Evaluate {

    public static Function<Snapshot, Optional<Transaction>> insuranceTransactions() {
       return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();
            final boolean insurancePurchased = actionsTaken.size() == 1 && actionsTaken.contains(BUY_INSURANCE);
            final LocalDateTime now = LocalDateTime.now();
            final UUID accountKey = snapshot.getAccountKey();
            final String description = BUY_INSURANCE.name();
            final int bet = snapshot.getNegativeBet();

           if (insurancePurchased) {
                return Optional.of(new Transaction(now, accountKey, description, bet));
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> doubleDownTransactions() {
        return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();

            if (actionsTaken.stream().filter(a -> a.equals(DOUBLE)).count() == 1) {
                final LocalDateTime now = LocalDateTime.now();
                final UUID accountKey = snapshot.getAccountKey();
                final String description = "DOUBLE";
                final int bet = snapshot.getNegativeBet();

                return Optional.of(new Transaction(now, accountKey, description, bet));
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> outcomeTransactions() {
        return (snapshot) -> {
            if (snapshot.isHandResolved()) {
                final LocalDateTime now = LocalDateTime.now();
                final UUID accountKey = snapshot.getAccountKey();
                final String description = snapshot.getOutcome().name();
                final int payout = settleBet(snapshot);

                return Optional.of(new Transaction(now, accountKey, description, payout));
            } else {
                return Optional.empty();
            }
        };
    }

    public static Function<Snapshot, Optional<Transaction>> splitTransactions() {
        return (snapshot) -> {
            final Collection<Action> actionsTaken = snapshot.getActionsTaken();
            final LocalDateTime now = LocalDateTime.now();
            final UUID accountKey = snapshot.getAccountKey();
            final String description = SPLIT.name();
            final int bet = snapshot.getNegativeBet();

            final boolean chargeForSplit = (actionsTaken.stream().anyMatch(a -> a.equals(SPLIT)) &&
                    actionsTaken.stream().noneMatch(a ->
                            a.equals(HIT) ||
                            a.equals(DOUBLE) ||
                            a.equals(STAND)));

            final Transaction transaction = new Transaction(now, accountKey, description, bet);

            if (chargeForSplit) {
                return Optional.of(transaction);
            } else {
                return Optional.empty();
            }
        };
    }
}
