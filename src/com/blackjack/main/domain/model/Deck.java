package com.blackjack.main.domain.model;

import java.util.Stack;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Deck extends Stack<Card> {

    private final UUID key;

    private Deck(UUID uuid) {
        this.key = uuid;
    }

    public static Deck emptySerializedDeck() {
        return new Deck(randomUUID());
    }

    public UUID key() {
        return key;
    }

    public Card drawCard() {
        return pop();
    }
}
