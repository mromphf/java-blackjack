package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.Card;

import java.util.Collection;

public class CardFunctions {

    public final static int MAXIMUM_SCORE = 21;
    public final static int ACE_HIGH_SCORE = 11;

    public static boolean atLeastOneAce(Collection<Card> hand) {
        return hand.stream().anyMatch(Card::isAce);
    }

    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.blackjackValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static boolean isPush(Collection<Card> handA, Collection<Card> handB) {
        return (score(handA) == score(handB)) && !isBust(handA);
    }

    public static boolean canSplit(Collection<Card> cards) {
        if (cards.size() == 2) {
            final int blackjackValue = cards.stream().findFirst().get().blackjackValue();
            return cards.stream().allMatch(card -> card.blackjackValue() == blackjackValue);
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
        } else if (atLeastOneAce(cards) && softTotalIsFavourable(cards)) {
            return softTotal(cards);
        } else {
            return total(cards);
        }
    }

    public static int concealedScore(Collection<Card> cards) {
        return cards.stream()
                .filter(Card::isFaceUp)
                .mapToInt(card -> card.isAce() ? ACE_HIGH_SCORE : card.blackjackValue())
                .sum();
    }

    public static int softTotal(Collection<Card> cards) {
        return atLeastOneAce(cards)
                ? total(cards) + 10
                : total(cards);
    }

    public static boolean softTotalIsFavourable(Collection<Card> cards) {
        return softTotal(cards) <= MAXIMUM_SCORE;
    }

    public static int total(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::blackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (isBust(dealerCards) && !isBust(playerCards) ||
                (!isBust(playerCards) && score(playerCards) > score(dealerCards)));
    }
}
