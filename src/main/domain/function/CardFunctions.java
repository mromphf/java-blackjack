package main.domain.function;

import main.domain.model.Card;

import java.util.Collection;
import java.util.Iterator;

public class CardFunctions {

    public final static int MAXIMUM_SCORE = 21;
    public final static int ACE_HIGH_SCORE = 11;

    public static boolean atLeastOneAce(Collection<Card> hand) {
        return hand.stream().anyMatch(Card::isAce);
    }

    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.getBlackjackValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static boolean isPush(Collection<Card> handA, Collection<Card> handB) {
        return (score(handA) == score(handB)) && !isBust(handA);
    }

    public static boolean canSplit(Collection<Card> cards) {
        if (cards.size() == 2) {
            final int blackjackValue = cards.stream().findFirst().get().getBlackjackValue();
            return cards.stream().allMatch(card -> card.getBlackjackValue() == blackjackValue);
        } else {
            return false;
        }
    }

    public static boolean isBust(Collection<Card> cards)  {
        return score(cards) > MAXIMUM_SCORE;
    }

    public static int score(Collection<Card> cards) {
        if (isBlackjack(cards)) {
            return MAXIMUM_SCORE;
        } else if (atLeastOneAce(cards) && hardTotalIsFavourable(cards)) {
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
        return atLeastOneAce(cards)
                ? softTotal(cards) + 10
                : softTotal(cards);
    }

    public static boolean hardTotalIsFavourable(Collection<Card> cards) {
        return hardTotal(cards) <= MAXIMUM_SCORE;
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::getBlackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (isBust(dealerCards) && !isBust(playerCards) ||
                (!isBust(playerCards) && score(playerCards) > score(dealerCards)));
    }
}
