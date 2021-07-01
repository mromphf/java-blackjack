package main.domain;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.format.DateTimeFormatter.*;

public class Account {

    private final UUID key;
    private final String name;
    private final int balance;
    private final LocalDateTime created;

    public Account(UUID key, String name, LocalDateTime created) {
        this.key = key;
        this.name = name;
        this.balance = 0;
        this.created = created;
    }

    private Account(UUID key, String name, int balance, LocalDateTime created) {
        this.key = key;
        this.name = name;
        this.balance = balance;
        this.created = created;
    }

    public static Account placeholder() {
        return new Account(UUID.randomUUID(), "Placeholder", LocalDateTime.now());
    }

    public UUID getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public int getBalance() {
        return balance;
    }

    public Account updateBalance(Transaction transaction) {
        final List<Transaction> transactions = new LinkedList<>();
        transactions.add(transaction);
        return updateBalance(transactions);
    }

    public Account updateBalance(Collection<Transaction> transactions) {
        return new Account(key, name, balance + deriveBalance(transactions), created);
    }

    private int deriveBalance(Collection<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getAccountKey().equals(key))
                .mapToInt(Transaction::getAmount)
                .sum();
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
