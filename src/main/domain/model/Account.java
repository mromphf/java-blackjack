package main.domain.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.*;
import static java.util.UUID.randomUUID;

public class Account implements Function<Transaction, Account> {

    private final UUID key;
    private final String name;
    private final int balance;
    private final LocalDateTime created;

    public static Account newAccount(String name) {
        return new Account(randomUUID(), name, (0), now());
    }

    public static Account account(UUID key, String name, int balance, LocalDateTime created) {
        return new Account(key, name, balance, created);
    }

    private Account(UUID key, String name, int balance, LocalDateTime created) {
        this.key = key;
        this.name = name;
        this.balance = balance;
        this.created = created;
    }

    public UUID key() {
        return key;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTimestamp() {
        return created;
    }

    public int getBalance() {
        return balance;
    }

    public Account debit(int debit) {
        return new Account(key, name, (balance + (debit * -1)), created);
    }

    @Override
    public Account apply(Transaction transaction) {
        return new Account(key, name, (balance + transaction.getAmount()), created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return balance == account.balance &&
                key.equals(account.key) &&
                name.equals(account.name) &&
                created.equals(account.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, balance);
    }

    @Override
    public String toString() {
        return String.format("%s: \t$%s\tCreated: %s", name, balance, created.format(ISO_DATE));
    }
}
