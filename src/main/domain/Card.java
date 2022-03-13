package main.domain;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;

public class Card {
    private final UUID key = randomUUID();
    private final int value;
    private final Suit suit;

    public Card(int value, Suit suit) throws IllegalArgumentException {
        if (value > 13 || value < 1) {
            throw new IllegalArgumentException("Card value must be greater than 0 and less than 14.");
        }
        this.value = value;
        this.suit = suit;
    }

    public boolean isAce() {
        return value == 1;
    }

    public int getBlackjackValue() {
        return Math.min(10, value);
    }

    public int getFaceValue() {
        return value;
    }

    public Suit getSuit() {
        return suit;
    }

    public String name() {
        return new HashMap<Integer, String>() {{
            put(1, "Ace");
            put(11, "Jack");
            put(12, "Queen");
            put(13, "King");
        }}.getOrDefault(value, String.valueOf(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value && Objects.equals(key, card.key) && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return hash(key, value, suit);
    }

    @Override
    public String toString() {
        return String.format("%s of %s", name(), suit);
    }
}
