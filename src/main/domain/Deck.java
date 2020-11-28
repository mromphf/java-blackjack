package main.domain;

import java.util.*;

public class Deck {
    private static final int CARDS_PER_SUIT = 13;

    public static Stack<Card> fresh() {
        Stack<Card> result = new Stack<>();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= CARDS_PER_SUIT; i++) {
                result.add(new Card(i, suit));
            }
        }
        return result;
    }

    public static Stack<Card> shuffle(List<Card> deck) {
        Stack<Card> stack = new Stack<>();
        while(!deck.isEmpty()) {
            Random r = new Random();
            int ri = r.nextInt(deck.size());
            stack.add(deck.get(ri));
            deck.remove(ri);
        }
        return stack;
    }

    public static Map<String, Collection<Card>> openingHand(Stack<Card> deck) {
        if (deck.size() < 4) {
            final String failureMsg = "Not enough cards to start a new round! Quitting...";
            System.out.println(failureMsg);
            System.exit(0);
            throw new IllegalStateException(failureMsg);
        } else {
            final List<Card> dealerHand = new LinkedList<>();
            final List<Card> playerHand = new LinkedList<>();

            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());
            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());

            return new HashMap<String, Collection<Card>>() {{
                put("dealer", dealerHand);
                put("player", playerHand);
            }};
        }
    }
}
