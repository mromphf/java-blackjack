package main.domain.model;

import java.util.UUID;

import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;

public class Card extends AnonymousCard {

    protected final UUID key;
    protected final int ordinal;

    private Card(int ordinal, UUID key, Rank rank, Suit suit, boolean isFaceUp) {
        super(suit, rank, isFaceUp);
        this.key = key;
        this.ordinal = ordinal;
    }

    protected Card(int ordinal, Rank rank, Suit suit, boolean isFaceUp) {
        super(suit, rank, isFaceUp);
        this.key = randomUUID();
        this.ordinal = ordinal;
    }

    public static Card card (Rank rank, Suit suit) {
        return new Card(1, rank, suit, true);
    }

    public static Card card(int ordinal, Rank rank, Suit suit) {
        return new Card(ordinal, rank, suit, (true));
    }

    public static Card card(int ordinal, Rank rank, Suit suit, boolean isFaceUp) {
        return new Card(ordinal, rank, suit, isFaceUp);
    }

    public Card faceDown() {
        return new Card(ordinal, key, rank, suit, (false));
    }

    public AnonymousCard anonymize() {
        return anonymousCard(suit, rank);
    }

    public UUID key() {
        return key;
    }

    public int ordinal() {
        return ordinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Card card = (Card) o;
        return key.equals(card.key);
    }

    @Override
    public int hashCode() {
        return hash(key, rank, suit);
    }

    @Override
    public String toString() {
        return String.format("%s of %s", rank.NAME, suit);
    }
}
