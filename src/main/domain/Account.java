package main.domain;

import main.common.Csv;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Account implements Csv {

    private static final String CSV_HEADER_ROW = "key,name,created";

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

    public Account updateBalance(Transaction transaction) {
        final List<Transaction> transactions = new LinkedList<>();
        transactions.add(transaction);
        return updateBalance(transactions);
    }

    public Account updateBalance(Collection<Transaction> transactions) {
        return new Account(key, name, balance + deriveBalance(transactions), created);
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
        return balance == account.balance && key.equals(account.key) && name.equals(account.name) && created.equals(account.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, balance);
    }

    @Override
    public String toString() {
        return String.format("%s: \t$%s\tCreated: %s", name, balance, created.format(ISO_DATE));
    }

    @Override
    public String header() {
        return CSV_HEADER_ROW;
    }

    @Override
    public String row() {
        String zonedTimestamp = created.atZone(systemDefault()).format(ISO_DATE_TIME);
        return String.format("%s,%s,%s", key, name, zonedTimestamp);
    }
}
