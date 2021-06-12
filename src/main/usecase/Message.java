package main.usecase;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Message {
    private final Predicate elm;
    private final Account account;
    private final Transaction transaction;
    private final List<Transaction> transactions;
    private final Collection<Account> accounts;

    private Message(Predicate elm, Account account, Transaction transaction, List<Transaction> transactions, Collection<Account> accounts) {
        this.elm = elm;
        this.account = account;
        this.transaction = transaction;
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public static Message of(Predicate elm, Account account) {
        return new Message(elm, account, Transaction.placeholder(), new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate elm, List<Transaction> transactions) {
        return new Message(elm, Account.placeholder(), Transaction.placeholder(), transactions, new LinkedList<>());
    }

    public static Message of(Predicate elm, Collection<Account> accounts) {
        return new Message(elm, Account.placeholder(), Transaction.placeholder(), new LinkedList<>(), accounts);
    }

    public static Message of(Predicate elm, Transaction transaction) {
        return new Message(elm, Account.placeholder(), transaction, new LinkedList<>(), new LinkedList<>());
    }

    public boolean is(Predicate elm) {
        return elm.equals(this.elm);
    }

    public Predicate getElm() {
        return elm;
    }

    public int getCurrentBalance() {
        return account.getBalance();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Account getAccount() {
        return account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Collection<Account> getAccounts() {
        return accounts;
    }
}
