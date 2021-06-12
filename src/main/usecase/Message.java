package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Transaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Message {
    private final int amount;
    private final Predicate elm;
    private final Account account;
    private final Transaction transaction;
    private final List<Transaction> transactions;
    private final Collection<Account> accounts;
    private final Action action;

    private Message(Predicate elm, int amount, Account account, Transaction transaction, Action action, List<Transaction> transactions, Collection<Account> accounts) {
        this.amount = amount;
        this.elm = elm;
        this.account = account;
        this.transaction = transaction;
        this.action = action;
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public static Message of(Predicate elm, Account account) {
        return new Message(elm, 0, account, Transaction.placeholder(), Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate predicate, int amount, Account account) {
        return new Message(predicate, amount, account, Transaction.placeholder(), Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate elm, List<Transaction> transactions) {
        return new Message(elm, 0, Account.placeholder(), Transaction.placeholder(), Action.PLACEHOLDER, transactions, new LinkedList<>());
    }

    public static Message of(Predicate elm, Collection<Account> accounts) {
        return new Message(elm, 0, Account.placeholder(), Transaction.placeholder(), Action.PLACEHOLDER, new LinkedList<>(), accounts);
    }

    public static Message of(Predicate elm, Transaction transaction) {
        return new Message(elm, 0, Account.placeholder(), transaction, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate predicate, Action action) {
        return new Message(predicate, 0, Account.placeholder(), Transaction.placeholder(), action, new LinkedList<>(), new LinkedList<>());
    }

    public boolean is(Predicate elm) {
        return elm.equals(this.elm);
    }

    public int getAmount() {
        return amount;
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

    public Action getAction() {
        return action;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Collection<Account> getAccounts() {
        return accounts;
    }
}
