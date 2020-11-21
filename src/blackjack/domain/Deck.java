package blackjack.domain;

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

    public static List<Card> burn(int cards, List<Card> deck) {
        return deck.subList(0, deck.size() - cards);
    }

    public static Map<String, List<Card>> openingHand(List<Card> deck) {
        // TODO: Need safety checks against null pointers

        final List<Card> dealerHand = new LinkedList<Card>() {{
            add(deck.get(1));
            add(deck.get(3));
        }};

        final List<Card> playerHand = new LinkedList<Card>() {{
            add(deck.get(0));
            add(deck.get(2));
        }};

        return new HashMap<String, List<Card>>() {{
            put("dealer", dealerHand);
            put("player", playerHand);
        }};
    }
}
