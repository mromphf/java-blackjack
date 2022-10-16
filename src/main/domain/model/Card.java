package main.domain.model;

import java.util.UUID;

import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;

public class Card extends AnonymousCard {

    protected final UUID key;
    protected final int ordinal = 0;

    private Card(UUID key, Rank rank, Suit suit, boolean isFaceUp) {
        super(suit, rank, isFaceUp);
        this.key = key;
    }

    protected Card(Rank rank, Suit suit, boolean isFaceUp) {
        super(suit, rank, isFaceUp);
        this.key = randomUUID();
    }

    public static Card card(Rank rank, Suit suit) {
        return new Card(rank, suit, (true));
    }

    public static Card card(Rank rank, Suit suit, boolean isFaceUp) {
        return new Card(rank, suit, isFaceUp);
    }

    public Card faceDown() {
        return new Card(key, rank, suit, (false));
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
