package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.util.Collection;
import java.util.List;

public class Event {
    public final Predicate predicate;
    public final Object data;

    public Event(Predicate predicate, Object data) {
        this.predicate = predicate;
        this.data = data;
    }

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public int getInt() {
        return (int) data;
    }

    public Snapshot getSnapshot() {
        return (Snapshot) data;
    }

    public Account getAccount() {
        return (Account) data;
    }

    public Transaction getTransaction() {
        return (Transaction) data;
    }

    public Action getAction() {
        return (Action) data;
    }

    public Layout getLayout() {
        return (Layout) data;
    }

    public List<Transaction> getTransactions() {
        return (List<Transaction>) data;
    }

    public Collection<Account> getAccounts() {
        return (Collection<Account>) data;
    }
}
