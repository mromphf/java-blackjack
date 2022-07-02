package main.domain.function;

import main.domain.model.AnonymousCard;
import main.domain.model.*;

import java.util.*;

import static main.domain.model.AnonymousCard.anonymousCard;
import static main.domain.model.Card.card;
import static main.domain.model.Hand.emptyHand;

public class Dealer {

    public static Deck freshDeck() {

        Deck result = new Deck();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(card(rank, suit));
            }
        }

        return result;
    }

    public static Collection<AnonymousCard> anonymousDeck() {

        Collection<AnonymousCard> result = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(anonymousCard(suit, rank));
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
            dealerHand.add(deck.pop().faceDown());

            return new HashMap<String, Hand>() {{
                put("dealer", dealerHand);
                put("player", playerHand);
            }};
        }
    }
}
