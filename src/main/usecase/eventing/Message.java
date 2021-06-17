package main.usecase.eventing;

import main.domain.Account;
import main.domain.Action;
import main.domain.Transaction;
import main.domain.Bet;
import main.usecase.Layout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Message {
    private final Predicate predicate;
    private final Transaction transaction;
    private final List<Transaction> transactions;

    private Message(Predicate predicate, Bet bet, Account account, Transaction transaction, Layout layout, Action action, List<Transaction> transactions, Collection<Account> accounts) {
        this.predicate = predicate;
        this.transaction = transaction;
        this.transactions = transactions;
    }

    public static Message of(Predicate elm, List<Transaction> transactions) {
        return new Message(elm, null, Account.placeholder(), Transaction.placeholder(), Layout.PLACEHOLDER, Action.PLACEHOLDER, transactions, new LinkedList<>());
    }

    public static Message of(Predicate elm, Transaction transaction) {
        return new Message(elm, null, Account.placeholder(), transaction, Layout.PLACEHOLDER, Action.PLACEHOLDER, new LinkedList<>(), new LinkedList<>());
    }

    public boolean is(Predicate predicate) {
        return predicate.equals(this.predicate);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
