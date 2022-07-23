package main.domain.model;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;
import static main.domain.model.AnonymousCard.anonymousCard;
import static main.domain.model.Rank.ACE;

public class Card {

    private final boolean isFaceUp;
    protected final UUID key;
    protected final Rank rank;
    protected final Suit suit;

    private Card(UUID key, Rank rank, Suit suit, boolean isFaceUp) {
        this.key = key;
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = isFaceUp;
    }

    protected Card(Rank rank, Suit suit, boolean isFaceUp) {
        this.key = randomUUID();
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = isFaceUp;
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

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public boolean isAce() {
        return rank == ACE;
    }

    public int blackjackValue() {
        return rank.BLACKJACK_VALUE;
    }

    public Rank rank() {
        return rank;
    }

    public Suit suit() {
        return suit;
    }

    public boolean is(Suit suit) {
        return this.suit == suit;
    }

    public AnonymousCard anonymize() {
        return anonymousCard(suit, rank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(key, card.key) && rank == card.rank && suit == card.suit;
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
