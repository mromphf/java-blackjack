package main.domain;

public enum Rank {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13);

    public final int VALUE;

    Rank(int value) {
        this.VALUE = value;
    }

    public static Rank of(int value) throws IllegalArgumentException {
        if (value > 0 && value < 14) {
            return values()[value];
        } else {
            throw new IllegalArgumentException("Rank Value must be greater than 0 and less than 14.");
        }

    }
}
