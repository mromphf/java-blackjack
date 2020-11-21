package blackjack.domain;

import java.util.Collection;

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

    public static int score(Collection<Card> cards) {
        //TODO: Soft total Ace logic
        if (isBlackjack(cards)) {
            return 21;
        } else {
            return cards.stream().mapToInt(Rules::normalizeFaceValues).sum();
        }
    }

    public static int concealedScore(Collection<Card> cards) {
        Card c = cards.iterator().next();
        return c.isAce() ? 11 : normalizeFaceValues(c);
    }

    private static int normalizeFaceValues(Card c) {
        return c.getValue() < 11 ? c.getValue() : 10;
    }
}
