package main.domain.function;

import main.domain.model.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static main.domain.model.Action.*;
import static main.domain.model.Outcome.*;

public class Rules {

    public final static int MAXIMUM_SCORE = 21;
    public final static int ACE_HIGH_SCORE = 11;

    public final static Predicate<Collection<Card>> hardTotalIsFavourable = cards -> hardTotal(cards) <= MAXIMUM_SCORE;

    public final static Predicate<Collection<Card>> atLeastOneAce = cards -> cards.stream().anyMatch(Card::isAce);

    public final static Predicate<Collection<Card>> isBust = cards -> score(cards) > MAXIMUM_SCORE;

    public final static BiPredicate<Collection<Card>, Collection<Card>> isPush = ((handA, handB) ->
            score(handA) == score(handB) && isBust.negate().test(handA));

    public final static IsBlackjack isBlackjack = new IsBlackjack();

    public final static Predicate<Collection<Card>> canSplit = cards -> {
        final Iterator<Card> iterator = cards.iterator();
        boolean twoCards = cards.size() == 2;
        if (twoCards) {
            return iterator.next().getBlackjackValue() == iterator.next().getBlackjackValue();
        } else {
            return false;
        }
    };

    public static int score(Collection<Card> cards) {
        if (isBlackjack.test(cards)) {
            return MAXIMUM_SCORE;
        } else if (atLeastOneAce.and(hardTotalIsFavourable).test(cards)) {
            return hardTotal(cards);
        } else {
            return softTotal(cards);
        }
    }

    public static int concealedScore(Collection<Card> cards) {
        final Iterator<Card> cardIterator = cards.iterator();
        if (cardIterator.hasNext()) {
            final Card card = cardIterator.next();
            return card.isAce() ? ACE_HIGH_SCORE : card.getBlackjackValue();
        } else {
            return 0;
        }
    }

    public static int hardTotal(Collection<Card> cards) {
        return atLeastOneAce.test(cards)
                ? softTotal(cards) + 10
                : softTotal(cards);
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::getBlackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (isBust.test(dealerCards)) && !isBust.test(playerCards) ||
                (!isBust.test(playerCards) && score(playerCards) > score(dealerCards));
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

        if ((isBust.test(playerHand) && !handsToPlay.isEmpty()) ||
                (!isBust.test(playerHand) &&
                        !handsToPlay.isEmpty() && standOrDouble) ||
                (!isBust.test(playerHand) &&
                        (actionsTaken.isEmpty() || !standOrDouble))) {
            return UNRESOLVED;
        } else if (playerWins(playerHand, dealerHand) && isBlackjack.test(playerHand)) {
            return BLACKJACK;
        } else if (playerWins(playerHand, dealerHand)) {
            return WIN;
        } else if (isPush.test(playerHand, dealerHand)) {
            return PUSH;
        } else if (isBust.test(playerHand)) {
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
                .anyMatch(a -> a.equals(BUY_INSURANCE)) && isBlackjack.test(snapshot.getDealerHand()))
                ? (bet * 2) : 0;

        final int betMultiplier = actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE)) ? 2 : 1;

        final float blackjackMultiplier = isBlackjack.test(playerHand) ? 1.5f : 1.0f;

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
}
