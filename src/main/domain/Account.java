package main.domain;

import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID key;
    private final String name;
    private final int balance;

    public Account(UUID key, String name, int balance) {
        this.key = key;
        this.name = name;
        this.balance = balance;
    }

    public Account updateBalance(int val) {
        return new Account(key, name, balance + val);
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
        return String.format("%s: \t$%s", name, balance);
    }
}
