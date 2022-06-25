package main.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction implements Comparable<Transaction> {

    private final LocalDateTime time;
    private final UUID accountKey;
    private final String description;
    private final int amount;

    private Transaction(LocalDateTime time, UUID accountKey, String description, int amount) {
        this.time = time;
        this.accountKey = accountKey;
        this.description = description;
        this.amount = amount;
    }

    public static Transaction transaction(LocalDateTime time, UUID accountKey, String description, Integer amount) {
        return new Transaction(time, accountKey, description, amount);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public UUID getAccountKey() {
        return accountKey;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
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

    @Override
    public int compareTo(Transaction target) {
        if (time.isAfter(target.getTime())) {
            return 1;
        } else if (time.isEqual(target.getTime())) {
            return 0;
        } else {
            return -1;
        }
    }
}
