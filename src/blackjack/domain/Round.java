package blackjack.domain;

import java.util.*;

public class Round {
    public static Map<String, Set<Card>> opening(Stack<Card> deck) {
        Set<Card> dealerHand = new HashSet<>();
        Set<Card> playerHand = new HashSet<>();

        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());

        return new HashMap<String, Set<Card>>() {{
            put("dealer", dealerHand);
            put("player", playerHand);
        }};
    }
}
