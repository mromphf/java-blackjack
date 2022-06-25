package main.domain.model;

public enum Rank {
    ACE(1, "Ace"),
    TWO(2, "Two"),
    THREE(3, "Three"),
    FOUR(4, "Four"),
    FIVE(5, "Five"),
    SIX(6, "Six"),
    SEVEN(7, "Seven"),
    EIGHT(8, "Eight"),
    NINE(9, "Nine"),
    TEN(10, "Ten"),
    JACK(11, "Jack"),
    QUEEN(12, "Queen"),
    KING(13, "King");

    public final int VALUE;
    public final String NAME;

    Rank(int value, String name) {
        this.VALUE = value;
        this.NAME = name;
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
