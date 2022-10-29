package com.blackjack.main.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction implements Comparable<Transaction> {

    private final LocalDateTime timestamp;
    private final UUID accountKey;
    private final String description;
    private final int amount;

    private Transaction(
            LocalDateTime timestamp,
            UUID accountKey,
            String description,
            int amount) {
        this.timestamp = timestamp;
        this.accountKey = accountKey;
        this.description = description;
        this.amount = amount;
    }

    public static Transaction transaction(
            LocalDateTime time,
            UUID accountKey,
            String description,
            Integer amount) {
        return new Transaction(time, accountKey, description, amount);
    }

    public static Transaction signingBonus(Account account) {
        return new Transaction(
                account.getTimestamp(), account.key(), ("SIGNING BONUS"), (200));
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public UUID accountKey() {
        return accountKey;
    }

    public int amount() {
        return amount;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return amount == that.amount &&
               timestamp.equals(that.timestamp) &&
               accountKey.equals(that.accountKey) &&
               description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, accountKey, description, amount);
    }

    @Override
    public int compareTo(Transaction target) {
        if (timestamp.isAfter(target.timestamp())) {
            return 1;
        } else if (timestamp.isEqual(target.timestamp())) {
            return 0;
        } else {
            return -1;
        }
    }
}
