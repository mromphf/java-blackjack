package main.usecase;

import main.domain.Account;
import main.domain.Transaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Message {
    private final NetworkElement elm;
    private final Account account;
    private final List<Transaction> transactions;
    private final Collection<Account> accounts;

    private Message(NetworkElement elm, Account account, List<Transaction> transactions, Collection<Account> accounts) {
        this.elm = elm;
        this.account = account;
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public static Message of(NetworkElement elm, Account account) {
        return new Message(elm, account, new LinkedList<>(), new LinkedList<>());
    }

    public static Message of(NetworkElement elm, List<Transaction> transactions) {
        return new Message(elm, Account.placeholder(), transactions, new LinkedList<>());
    }

    public static Message of(NetworkElement elm, Collection<Account> accounts) {
        return new Message(elm, Account.placeholder(), new LinkedList<>(), accounts);
    }

    public boolean is(NetworkElement elm) {
        return elm.equals(this.elm);
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

    public Collection<Account> getAccounts() {
        return accounts;
    }
}
