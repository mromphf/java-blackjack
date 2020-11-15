package blackjack.domain;

public class Card {
    private int value;
    private Suit suit;

    public Card(int value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public boolean isFace() {
        return value > 10;
    }

    public boolean isAce() {
        return value == 1;
    }

    public int getValue() {
        return value;
    }
}
