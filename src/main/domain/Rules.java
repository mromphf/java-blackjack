package main.domain;

import java.util.*;
import java.util.function.Predicate;

import static main.domain.Action.*;
import static main.domain.Outcome.*;

public class Rules {

    public final static int MAXIMUM_SCORE = 21;

    public final static Predicate<Collection<Card>> AT_LEAST_ONE_ACE = cards -> cards.stream().anyMatch(Card::isAce);

    public final static Predicate<Collection<Card>> IS_BUST = cards -> score(cards) > MAXIMUM_SCORE;

    public final static IsBlackjack IS_BLACKJACK = new IsBlackjack();
    public final static IsInsuranceAvailable IS_INSURANCE_AVAILABLE = new IsInsuranceAvailable();

    public static boolean isPush(Collection<Card> playerHand, Collection<Card> dealerHand) {
        return score(playerHand) == score(dealerHand) && !IS_BUST.test(playerHand);
    }

    public static boolean canSplit(Collection<Card> cards) {
        final Iterator<Card> iterator = cards.iterator();
        boolean twoCards = cards.size() == 2;
        if (twoCards) {
            return iterator.next().getBlackjackValue() == iterator.next().getBlackjackValue();
        } else {
            return false;
        }
    }

    public static int score(Collection<Card> cards) {
        if (IS_BLACKJACK.test(cards)) {
            return 21;
        } else if (AT_LEAST_ONE_ACE.test(cards) && hardTotalFavorable(cards)) {
            return hardTotal(cards);
        } else {
            return softTotal(cards);
        }
    }

    public static int concealedScore(Collection<Card> cards) {
        Iterator<Card> cardIterator = cards.iterator();
        if (cardIterator.hasNext()) {
            Card c = cardIterator.next();
            return c.isAce() ? 11 : c.getBlackjackValue();
        } else {
            return 0;
        }
    }

    public static boolean hardTotalFavorable(Collection<Card> cards) {
        return hardTotal(cards) <= 21;
    }

    public static int hardTotal(Collection<Card> cards) {
        return AT_LEAST_ONE_ACE.test(cards)
                ? softTotal(cards) + 10
                : softTotal(cards);
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::getBlackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (IS_BUST.test(dealerCards)) && !IS_BUST.test(playerCards) ||
                (!IS_BUST.test(playerCards) && score(playerCards) > score(dealerCards));
    }

    public static Outcome determineOutcome(Snapshot snapshot) {
        return determineOutcome(snapshot.getActionsTaken(),
                snapshot.getPlayerHand(),
                snapshot.getDealerHand(),
                snapshot.getHandsToPlay());
    }

    public static Outcome determineOutcome(Collection<Action> actionsTaken,
                                           Collection<Card> playerHand,
                                           Collection<Card> dealerHand,
                                           Collection<Hand> handsToPlay) {

        final boolean standOrDouble = actionsTaken.stream()
                .anyMatch(a -> a.equals(STAND) || a.equals(DOUBLE));

        if ((IS_BUST.test(playerHand) && !handsToPlay.isEmpty()) ||
                (!IS_BUST.test(playerHand) &&
                        !handsToPlay.isEmpty() && standOrDouble) ||
                (!IS_BUST.test(playerHand) &&
                        (actionsTaken.isEmpty() || !standOrDouble))) {
            return UNRESOLVED;
        } else if (playerWins(playerHand, dealerHand) && IS_BLACKJACK.test(playerHand)) {
            return BLACKJACK;
        } else if (playerWins(playerHand, dealerHand)) {
            return WIN;
        } else if (isPush(playerHand, dealerHand)) {
            return PUSH;
        } else if (IS_BUST.test(playerHand)) {
            return BUST;
        } else {
            return LOSE;
        }
    }

    public static int settleBet(Snapshot snapshot) {
        final Collection<Card> playerHand = snapshot.getPlayerHand();
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final int bet = snapshot.getBet();

        final int insurancePayout = (actionsTaken.stream()
                .anyMatch(a -> a.equals(BUY_INSURANCE)) && IS_BLACKJACK.test(snapshot.getDealerHand()))
                ? (bet * 2) : 0;

        final int betMultiplier = actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE)) ? 2 : 1;

        final float blackjackMultiplier = IS_BLACKJACK.test(playerHand) ? 1.5f : 1.0f;

        switch (determineOutcome(snapshot)) {
            case BLACKJACK:
            case WIN:
                return (((int) (bet * blackjackMultiplier) * 2) * betMultiplier) + insurancePayout;
            case PUSH:
                return (bet * betMultiplier) + insurancePayout;
            default:
                return 0;
        }
    }

    public static class IsBlackjack implements Predicate<Collection<Card>> {
        @Override
        public boolean test(Collection<Card> cards) {
            boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
            boolean tenOrHigher = cards.stream().filter(c -> c.getBlackjackValue() > 9).count() == 1;
            boolean twoCards = cards.size() == 2;
            return twoCards && oneAce && tenOrHigher;
        }
    }

    public static class IsInsuranceAvailable implements Predicate<Collection<Card>> {
        @Override
        public boolean test(Collection<Card> dealerHand) {
            if (dealerHand.size() > 0) {
                // TODO: This is broken as long as Hand is using Set logic
                return dealerHand.iterator().next().isAce();
            }
            return false;
        }
    }
}
