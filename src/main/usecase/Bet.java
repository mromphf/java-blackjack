package main.usecase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Bet {

    private final LocalDateTime timestamp;
    private final UUID accountKey;
    private final int val;

    private Bet(LocalDateTime timestamp, UUID accountKey, int val) {
        this.timestamp = timestamp;
        this.accountKey = accountKey;
        this.val = val;
    }

    public static Bet of(LocalDateTime timestamp, UUID accountKey, int val) {
        return new Bet(timestamp, accountKey, val);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public UUID getAccountKey() {
        return accountKey;
    }

    public int getVal() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bet bet = (Bet) o;
        return val == bet.val && timestamp.equals(bet.timestamp) && accountKey.equals(bet.accountKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, accountKey, val);
    }
}
