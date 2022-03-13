package main.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Dealer {
    private static final int CARDS_PER_SUIT = 13;
    private static final int DECKS = 4;

    public static Deck freshDeck() {
        Deck result = new Deck();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= DECKS ; i++) {
                for (int j = 1; j <= CARDS_PER_SUIT; j++)
                result.add(new Card(i, suit));
            }
        }
        return result;
    }

    public static Deck freshlyShuffledDeck() {
        return shuffle(freshDeck());
    }

    public static Deck shuffle(Deck deck) {
        Deck stack = new Deck();
        while(!deck.isEmpty()) {
            Random r = new Random();
            int ri = r.nextInt(deck.size());
            stack.add(deck.get(ri));
            deck.remove(ri);
        }
        return stack;
    }

    public static Map<String, Hand> openingHand(Stack<Card> deck) {
        if (deck.size() < 4) {
            throw new IllegalArgumentException("Not enough cards to start a new round!");
        } else {
            final Hand dealerHand = new Hand();
            final Hand playerHand = new Hand();

            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());
            playerHand.add(deck.pop());
            dealerHand.add(deck.pop());

            return new HashMap<String, Hand>() {{
                put("dealer", dealerHand);
                put("player", playerHand);
            }};
        }
    }
}
