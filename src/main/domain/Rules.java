package main.domain;

import java.util.Collection;
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
        //TODO: Need check for empty iterator
        Card c = cards.iterator().next();
        return c.isAce() ? 11 : normalizeFaceValue(c);
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

    public static boolean playerWins(Map<String, ? extends Collection<Card>> hands) {
        return score(hands.get("player")) > score(hands.get("dealer"));
    }
}
