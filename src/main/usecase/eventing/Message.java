package main.usecase.eventing;

import main.domain.Account;
import main.domain.Action;
import main.domain.Transaction;
import main.usecase.Bet;
import main.usecase.Layout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Message {
    private final Bet bet;
    private final Predicate predicate;
    private final Account account;
    private final Transaction transaction;
    private final Layout layout;
    private final List<Transaction> transactions;
    private final Collection<Account> accounts;
    private final Action action;

    private Message(Predicate predicate, Bet bet, Account account, Transaction transaction, Layout layout, Action action, List<Transaction> transactions, Collection<Account> accounts) {
        this.bet = bet;
        this.predicate = predicate;
        this.account = account;
        this.transaction = transaction;
        this.layout = layout;
        this.action = action;
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public static Message of(Predicate elm, Account account) {
        return new Message(elm, null, account, Transaction.placeholder(), Layout.PLACEHOLDER, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate predicate, Bet bet) {
        return new Message(predicate, bet, Account.placeholder(), Transaction.placeholder(), Layout.PLACEHOLDER, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate elm, List<Transaction> transactions) {
        return new Message(elm, null, Account.placeholder(), Transaction.placeholder(), Layout.PLACEHOLDER, Action.PLACEHOLDER, transactions, new LinkedList<>());
    }

    public static Message of(Predicate elm, Collection<Account> accounts) {
        return new Message(elm, null, Account.placeholder(), Transaction.placeholder(), Layout.PLACEHOLDER, Action.PLACEHOLDER, new LinkedList<>(), accounts);
    }

    public static Message of(Predicate elm, Transaction transaction) {
        return new Message(elm, null, Account.placeholder(), transaction, Layout.PLACEHOLDER, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate predicate, Action action) {
        return new Message(predicate, null, Account.placeholder(), Transaction.placeholder(), Layout.PLACEHOLDER, action, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(Predicate predicate, Layout layout) {
        return new Message(predicate, null, Account.placeholder(), Transaction.placeholder(), layout, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public boolean is(Predicate predicate) {
        return predicate.equals(this.predicate);
    }

    public Bet getBet() {
        return bet;
    }

    public Predicate getPredicate() {
        return predicate;
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

    public Layout getLayout() {
        return layout;
    }
}
