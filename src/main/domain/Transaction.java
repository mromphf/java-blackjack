package main.domain;

import main.common.Csv;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Transaction implements Csv, Comparable<Transaction> {

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
    public String toCsvRow() {
        String zonedTimestamp = time.atZone(systemDefault()).format(ISO_DATE_TIME);
        return String.format("%s,%s,%s,%s", zonedTimestamp, accountKey, description, amount);
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
