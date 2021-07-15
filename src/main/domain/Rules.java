package main.domain;

import java.util.*;

import static main.domain.Action.*;
import static main.domain.Outcome.*;

public class Rules {
    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.getBlackjackValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static boolean isBust(Collection<Card> cards) {
        return score(cards) > 21;
    }

    public static boolean isPush(Collection<Card> playerHand, Collection<Card> dealerHand) {
        return score(playerHand) == score(dealerHand) && !isBust(playerHand);
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
        if (isBlackjack(cards)) {
            return 21;
        } else if (atLeastOneAce(cards) && hardTotalFavorable(cards)) {
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

    public static boolean atLeastOneAce(Collection<Card> cards) {
        return cards.stream().anyMatch(Card::isAce);
    }

    public static boolean hardTotalFavorable(Collection<Card> cards) {
        return hardTotal(cards) <= 21;
    }

    public static int hardTotal(Collection<Card> cards) {
        return atLeastOneAce(cards)
                ? softTotal(cards) + 10
                : softTotal(cards);
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::getBlackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (isBust(dealerCards) && !isBust(playerCards)) ||
                (!isBust(playerCards) && score(playerCards) > score(dealerCards));
    }

    public static boolean insuranceAvailable(Collection<Card> dealerHand) {
        if (dealerHand.size() > 0) {
            return dealerHand.iterator().next().isAce();
        }
        return false;
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
                                           Stack<Stack<Card>> handsToPlay) {

        final boolean standOrDouble = actionsTaken.stream()
                .anyMatch(a -> a.equals(STAND) || a.equals(DOUBLE));

        if ((isBust(playerHand) && !handsToPlay.isEmpty()) ||
                (!isBust(playerHand) &&
                        !handsToPlay.isEmpty() && standOrDouble) ||
                (!isBust(playerHand) &&
                        (actionsTaken.isEmpty() || !standOrDouble))) {
            return UNRESOLVED;
        } else if (playerWins(playerHand, dealerHand) && isBlackjack(playerHand)) {
            return BLACKJACK;
        } else if (playerWins(playerHand, dealerHand)) {
            return WIN;
        } else if (isPush(playerHand, dealerHand)) {
            return PUSH;
        } else if (isBust(playerHand)) {
            return BUST;
        } else {
            return LOSE;
        }
    }

    public static int settleBet(Snapshot snapshot) {
        final Stack<Card> playerHand = snapshot.getPlayerHand();
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final int bet = snapshot.getBet();

        final int insurancePayout = (actionsTaken.stream()
                .anyMatch(a -> a.equals(BUY_INSURANCE)) && isBlackjack(snapshot.getDealerHand()))
                ? (bet * 2) : 0;

        final int betMultiplier = actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE)) ? 2 : 1;

        final float blackjackMultiplier = isBlackjack(playerHand) ? 1.5f : 1.0f;

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
}
