package main.domain.model;

import java.util.Objects;
import java.util.UUID;

import static java.lang.Boolean.*;
import static java.lang.Math.min;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;
import static main.domain.model.Rank.ACE;

public class Card {

    private static final int HIGHEST_POSSIBLE_VALUE = 10;

    private final Boolean isFaceUp;
    private final UUID key = randomUUID();
    private final Rank rank;
    private final Suit suit;

    private Card(Rank rank, Suit suit, Boolean isFaceUp) {
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = isFaceUp;
    }

    public static Card card(Rank rank, Suit suit) {
        return new Card(rank, suit, TRUE);
    }

    public static Card card(Rank rank, Suit suit, Boolean isFaceUp) {
        return new Card(rank, suit, isFaceUp);
    }

    public Card faceDown() {
        return new Card(rank, suit, FALSE);
    }

    public Boolean isFaceUp() {
        return isFaceUp;
    }

    public boolean isAce() {
        return rank == ACE;
    }

    public int getBlackjackValue() {
        return min(HIGHEST_POSSIBLE_VALUE, rank.VALUE);
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean is(Suit suit) {
        return this.suit == suit;
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
