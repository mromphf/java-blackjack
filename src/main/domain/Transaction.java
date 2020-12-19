package main.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final LocalDateTime time;
    private final UUID accountKey;
    private final String description;
    private final int amount;

    public Transaction(LocalDateTime time, UUID accountKey, String description, int amount) {
        this.time = time;
        this.accountKey = accountKey;
        this.description = description;
        this.amount = amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public UUID getAccountKey() {
        return accountKey;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return amount == that.amount &&
               time.equals(that.time) &&
               accountKey.equals(that.accountKey) &&
               description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, accountKey, description, amount);
    }
}
