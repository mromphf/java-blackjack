package main.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID key;
    private final String name;
    private final int balance;
    private final LocalDateTime created;

    public Account(UUID key, String name, int balance, LocalDateTime created) {
        this.key = key;
        this.name = name;
        this.balance = balance;
        this.created = created;
    }

    public Account updateBalance(int val) {
        return new Account(key, name, balance + val, created);
    }

    public UUID getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return balance == account.balance && key.equals(account.key) && name.equals(account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, balance);
    }

    @Override
    public String toString() {
        return String.format("%s: \t$%s\tCreated: %s", name, balance, created);
    }
}