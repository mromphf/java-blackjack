package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.*;

import java.util.Collection;
import java.util.Random;

import static com.blackjack.main.domain.model.Card.card;
import static com.blackjack.main.domain.model.Deck.emptySerializedDeck;
import static java.util.stream.Collectors.toSet;

public class DealerFunctions {

    public static Deck freshDeck() {
        final Deck result = emptySerializedDeck();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(card(rank, suit));
            }
        }

        return result;
    }

    public static Collection<AnonymousCard> anonymousDeck() {
        return freshDeck().stream()
                .map(Card::anonymize)
                .collect(toSet());
    }

    public static Deck shuffle(Deck deck) {
        Deck stack = emptySerializedDeck();
        int i = 0;
        while(!deck.isEmpty()) {
            Random r = new Random();
            int ri = r.nextInt(deck.size());
            stack.add(deck.get(ri).assignOrdinal(++i));
            deck.remove(ri);
        }
        return stack;
    }

    public static Deck shuffledFreshDeck() {
        return shuffle(freshDeck());
    }
}
