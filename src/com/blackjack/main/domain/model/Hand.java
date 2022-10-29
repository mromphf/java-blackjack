package com.blackjack.main.domain.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;

public class Hand extends LinkedList<Card> {

    private final UUID key = randomUUID();

    private Hand() {
        super();
    }

    private Hand(Collection<Card> cards) {
        super(cards);
    }

    public static Hand emptyHand() {
        return new Hand();
    }

    public static Hand handOf(Card... cards) {
        return new Hand(asList(cards));
    }

    public UUID key() {
        return key;
    }

    @Override
    public int hashCode() {
        return hash(key);
    }
}
