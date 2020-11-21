package blackjack.domain;

import java.util.Collection;

public class Rules {
    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.getValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static int score(Collection<Card> cards) {
        //TODO: Ace logic
        return cards.stream().mapToInt(Rules::normalizeFaceValues).sum();
    }

    private static int normalizeFaceValues(Card c) {
        return c.getValue() < 11 ? c.getValue() : 10;
    }
}
