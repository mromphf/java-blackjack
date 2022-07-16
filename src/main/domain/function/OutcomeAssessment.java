package main.domain.function;

import main.domain.Assessment;
import main.domain.model.*;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.function.CardFunctions.isBlackjack;
import static main.domain.model.Action.DOUBLE;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.LowOrderPredicate.outcomeIsResolved;
import static main.domain.predicate.LowOrderPredicate.playerPurchasedInsurance;

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
                    settleBet(snapshot)));
        } else {
            return empty();
        }
    }

    public static int settleBet(Snapshot snapshot) {
        final Collection<Card> playerHand = snapshot.getPlayerHand();
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final Outcome outcome = snapshot.getOutcome();
        final int bet = snapshot.getBet();

        final int insurancePayout = (playerPurchasedInsurance.test(snapshot) && isBlackjack(snapshot.getDealerHand()))
                ? (bet * 2) : 0;

        final int betMultiplier = actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE)) ? 2 : 1;

        final float blackjackMultiplier = isBlackjack(playerHand) ? 1.5f : 1.0f;

        switch (outcome) {
            case BLACKJACK:
            case WIN:
                return (((int) (bet * blackjackMultiplier) * 2) * betMultiplier) + insurancePayout;
            case PUSH:
                return (bet * betMultiplier) + insurancePayout;
            default:
                return 0;
        }
    }
}
