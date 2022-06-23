package main.domain;

import java.util.Objects;
import java.util.UUID;

import static java.lang.Math.min;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;
import static main.domain.Rank.ACE;

public class Card {

    private static final int HIGHEST_POSSIBLE_VALUE = 10;

    private final UUID key = randomUUID();
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
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

    public String name() {
        return rank.NAME;
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
        return String.format("%s of %s", name(), suit);
    }
}
