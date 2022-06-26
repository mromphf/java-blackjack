package main.domain.model;

public enum Rank {
    ACE(1, "Ace", 1),
    TWO(2, "Two", 2),
    THREE(3, "Three", 3),
    FOUR(4, "Four", 4),
    FIVE(5, "Five", 5),
    SIX(6, "Six", 6),
    SEVEN(7, "Seven", 7),
    EIGHT(8, "Eight", 8),
    NINE(9, "Nine", 9),
    TEN(10, "Ten", 10),
    JACK(11, "Jack", 10),
    QUEEN(12, "Queen", 10),
    KING(13, "King", 10);

    public final int ORDINAL;
    public final String NAME;
    public final Integer BLACKJACK_VALUE;

    Rank(int value, String name, Integer blackjackValue) {
        this.ORDINAL = value;
        this.NAME = name;
        this.BLACKJACK_VALUE = blackjackValue;
    }

    public static Rank of(int value) throws IllegalArgumentException {
        if (value > 0 && value < 14) {
            return values()[value];
        } else {
            throw new IllegalArgumentException("Rank Value must be greater than 0 and less than 14.");
        }
    }

    @Override
    public String toString() {
        return NAME;
    }
}
