package blackjack.domain;

import java.util.Stack;

public class Deck {
    public static Stack<Card> shuffle() {
        Stack<Card> result = new Stack<>();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i < 14; i++) {
                result.add(new Card(i, suit));
            }
        }
        return result;
    }
}
