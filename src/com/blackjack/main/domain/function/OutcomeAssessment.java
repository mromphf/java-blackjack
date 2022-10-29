package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.*;

import java.util.Collection;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static com.blackjack.main.domain.function.CardFunctions.isBlackjack;
import static com.blackjack.main.domain.model.Action.DOUBLE;
import static com.blackjack.main.domain.model.Transaction.transaction;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.insurancePurchased;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.outcomeIsResolved;

public class OutcomeAssessment implements Assessment {

    public static OutcomeAssessment outcomeAssessment() {
        return new OutcomeAssessment();
    }

    @Override
    public Optional<Transaction> apply(TableView tableView) {
        if (outcomeIsResolved.test(tableView)) {
            return Optional.of(transaction(
                    now(),
                    tableView.playerAccountKey(),
                    tableView.outcome().name(),
                    settleBet(tableView)));
        } else {
            return empty();
        }
    }

    public static int settleBet(TableView tableView) {
        final Collection<Card> playerHand = tableView.playerHand();
        final Collection<Action> actionsTaken = tableView.actionsTaken();
        final Outcome outcome = tableView.outcome();
        final int bet = tableView.bet();

        final int insurancePayout = (insurancePurchased.test(tableView) && isBlackjack(tableView.dealerHand()))
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
