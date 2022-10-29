package com.blackjack.main.domain.model;

import static java.util.Objects.hash;
import static com.blackjack.main.domain.model.Rank.ACE;

public class AnonymousCard {

    protected final boolean isFaceUp;
    protected final Rank rank;
    protected final Suit suit;

    protected AnonymousCard(Suit suit, Rank rank) {
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = false;
    }

    protected AnonymousCard(Suit suit, Rank rank, boolean isFaceUp) {
        this.suit = suit;
        this.rank = rank;
        this.isFaceUp = isFaceUp;
    }

    public static AnonymousCard anonymousCard(Suit suit, Rank rank) {
        return new AnonymousCard(suit, rank);
    }

    public static AnonymousCard anonymousCard(Suit suit, Rank rank, boolean isFaceUp) {
        return new AnonymousCard(suit, rank, isFaceUp);
    }

    public String shortName() {
        return suit.name().toLowerCase() + rank().ORDINAL;
    }

    public AnonymousCard faceDown() {
        return new AnonymousCard(suit, rank, (false));
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

    public Suit suit() {
        return suit;
    }

    public Rank rank() {
        return rank;
    }

    public boolean is(Suit suit) {
        return this.suit == suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnonymousCard that = (AnonymousCard) o;

        if (isFaceUp != that.isFaceUp) return false;
        if (rank != that.rank) return false;
        return suit == that.suit;
    }

    @Override
    public int hashCode() {
        return hash(rank, suit);
    }
}
