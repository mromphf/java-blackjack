package main.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Rules {
    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.getValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static boolean bust(Collection<Card> cards) {
        return score(cards) > 21;
    }

    public static boolean push(Collection<Card> hand1, Collection<Card> hand2) {
        return score(hand1) == score(hand2);
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
            return c.isAce() ? 11 : normalizeFaceValue(c);
        } else {
            return 0;
        }
    }

    public static boolean atLeastOneAce(Collection<Card> cards) {
        return cards.stream().anyMatch(Card::isAce);
    }

    public static boolean hardTotalFavorable(Collection<Card> cards) {
        return hardTotal(cards) < 21;
    }

    public static int normalizeFaceValue(Card c) {
        return c.getValue() < 11 ? c.getValue() : 10;
    }

    public static int hardTotal(Collection<Card> cards) {
        return atLeastOneAce(cards)
            ? softTotal(cards) + 10
            : softTotal(cards);
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Rules::normalizeFaceValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return bust(dealerCards) || score(playerCards) > score(dealerCards);
    }
}
