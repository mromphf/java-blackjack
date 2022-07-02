package main.domain.model;

import java.util.Objects;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.hash;
import static java.util.UUID.randomUUID;
import static main.domain.model.AnonymousCard.anonymousCard;
import static main.domain.model.Rank.ACE;

public class Card {

    private final Boolean isFaceUp;
    protected final UUID key;
    protected final Rank rank;
    protected final Suit suit;

    private Card(UUID key, Rank rank, Suit suit, Boolean isFaceUp) {
        this.key = key;
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = isFaceUp;
    }

    protected Card(Rank rank, Suit suit, Boolean isFaceUp) {
        this.key = randomUUID();
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
        return new Card(key, rank, suit, FALSE);
    }

    public Boolean isFaceUp() {
        return isFaceUp;
    }

    public boolean isAce() {
        return rank == ACE;
    }

    public int getBlackjackValue() {
        return rank.BLACKJACK_VALUE;
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
