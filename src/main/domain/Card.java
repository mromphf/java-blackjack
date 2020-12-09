package main.domain;

import java.util.Objects;
import java.util.Random;

public class Card {
    private final int faceValue;
    private final Suit suit;

    public Card(int value, Suit suit) {
        if (value > 13 || value < 1) {
            throw new IllegalArgumentException("Card value must be greater than 0 and less than 14.");
        }
        this.faceValue = value;
        this.suit = suit;
    }

    public boolean isAce() {
        return faceValue == 1;
    }

    public int getBlackjackValue() {
        return Math.min(10, faceValue);
    }

    public int getFaceValue() {
        return faceValue;
    }

    public Suit getSuit() {
        return suit;
    }

    public static Card random() {
        Random r = new Random();
        int v = r.nextInt(13) + 1;
        int s = r.nextInt(4);
        return new Card(v, Suit.values()[s]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return faceValue == card.faceValue &&
                suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(faceValue, suit);
    }
}
