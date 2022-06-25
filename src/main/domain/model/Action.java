package main.domain.model;

public enum Action {
    BUY_INSURANCE,
    DOUBLE,
    HIT,
    PLAY_NEXT_HAND,
    REFILL,
    SETTLE,
    SPLIT,
    STAND,
    WAIVE_INSURANCE
    ;

    public boolean turnEnded() {
        return this == DOUBLE || this == STAND;
    }
}
