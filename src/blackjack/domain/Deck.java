package blackjack.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Deck {
    public static Collection<Card> shuffle() {
        Set<Card> result = new HashSet<>();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i < 14; i++) {
                result.add(new Card(i, suit));
            }
        }
        return result;
    }
}
