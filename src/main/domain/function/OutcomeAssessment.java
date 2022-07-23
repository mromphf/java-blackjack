package main.domain.function;

import main.domain.model.*;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.empty;
import static main.domain.function.CardFunctions.isBlackjack;
import static main.domain.model.Action.DOUBLE;
import static main.domain.model.Transaction.transaction;
import static main.domain.predicate.LowOrderPredicate.outcomeIsResolved;
import static main.domain.predicate.LowOrderPredicate.insurancePurchased;

public class OutcomeAssessment implements Assessment {

    public static OutcomeAssessment outcomeAssessment() {
        return new OutcomeAssessment();
    }

    @Override
    public Optional<Transaction> apply(Table table) {
        if (outcomeIsResolved.test(table)) {
            return Optional.of(transaction(
                    table.timestamp(),
                    table.playerAccountKey(),
                    table.outcome().name(),
                    settleBet(table)));
        } else {
            return empty();
        }
    }

    public static int settleBet(Table table) {
        final Collection<Card> playerHand = table.playerHand();
        final Collection<Action> actionsTaken = table.actionsTaken();
        final Outcome outcome = table.outcome();
        final int bet = table.bet();

        final int insurancePayout = (insurancePurchased.test(table) && isBlackjack(table.dealerHand()))
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
