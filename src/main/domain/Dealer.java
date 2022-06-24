package main.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import static main.domain.Hand.emptyHand;

public class Dealer {

    public static Deck freshDeck() {

        Deck result = new Deck();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(rank, suit));
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
            final Hand dealerHand = emptyHand();
            final Hand playerHand = emptyHand();

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
