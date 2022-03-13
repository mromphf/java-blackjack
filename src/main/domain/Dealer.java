package main.domain;

import java.util.*;

public class Dealer {
    private static final int CARDS_PER_SUIT = 13;
    private static final int DECKS = 4;

    public static Stack<Card> freshDeck() {
        Stack<Card> result = new Stack<>();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= DECKS ; i++) {
                for (int j = 1; j <= CARDS_PER_SUIT; j++)
                result.add(new Card(i, suit));
            }
        }
        return result;
    }

    public static Stack<Card> freshlyShuffledDeck() {
        return shuffle(freshDeck());
    }

    public static Stack<Card> shuffle(Stack<Card> deck) {
        Stack<Card> stack = new Stack<>();
        while(!deck.isEmpty()) {
            Random r = new Random();
            int ri = r.nextInt(deck.size());
            stack.add(deck.get(ri));
            deck.remove(ri);
        }
        return stack;
    }

    public static Map<String, Stack<Card>> openingHand(Stack<Card> deck) {
        if (deck.size() < 4) {
            throw new IllegalArgumentException("Not enough cards to start a new round!");
        } else {
            final Stack<Card> dealerHand = new Stack<>();
            final Stack<Card> playerHand = new Stack<>();

            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());
            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());

            return new HashMap<String, Stack<Card>>() {{
                put("dealer", dealerHand);
                put("player", playerHand);
            }};
        }
    }
}
