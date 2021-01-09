package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.usecase.DataKey.*;
import static main.usecase.DataKey.TRANSACTION;
import static main.usecase.Predicate.*;

public class Event {
    public final Predicate predicate;
    public final Map<DataKey, Object> data;

    public Event(Predicate predicate, Map<DataKey, Object> data) {
        this.predicate = predicate;
        this.data = data;
    }

    public static Event balanceUpdated(Account account) {
        return new Event(BALANCE_UPDATED, new HashMap<DataKey, Object>() {{
            put(ACCOUNT, account);
        }});
    }

    public static Event layoutChanged(Layout layout) {
        return new Event(LAYOUT_CHANGED, new HashMap<DataKey, Object>() {{
            put(LAYOUT, layout);
        }});
    }

    public static Event accountSelected(Account account) {
        return new Event(ACCOUNT_SELECTED, new HashMap<DataKey, Object>() {{
            put(ACCOUNT, account);
        }});
    }

    public static Event gameStateUpdated(Snapshot snapshot) {
        return new Event(GAME_STATE_CHANGED, new HashMap<DataKey, Object>() {{
            put(SNAPSHOT, snapshot);
        }});
    }

    public static Event accountOpened(Account account) {
        return new Event(ACCOUNT_OPENED, new HashMap<DataKey, Object>() {{
            put(ACCOUNT, account);
        }});
    }

    public static Event accountDeleted(Account account) {
        return new Event(ACCOUNT_DELETED, new HashMap<DataKey, Object>() {{
            put(ACCOUNT, account);
        }});
    }

    public static Event actionTaken(Action action) {
        return new Event(ACTION_TAKEN, new HashMap<DataKey, Object>() {{
            put(ACTION, action);
        }});
    }

    public static Event betPlaced(int amount) {
        return new Event(BET_PLACED, new HashMap<DataKey, Object>() {{
            put(INT, amount);
        }});
    }

    public static Event accountsLoaded(Collection<Account> accounts) {
        return new Event(ACCOUNTS_LOADED, new HashMap<DataKey, Object>() {{
            put(ACCOUNTS, accounts);
        }});
    }

    public static Event transactionsLoaded(Collection<Transaction> transactions) {
        return new Event(TRANSACTIONS_LOADED, new HashMap<DataKey, Object>() {{
            put(DataKey.TRANSACTIONS, transactions);
        }});
    }

    public static Event onTransaction(Transaction t) {
        return new Event(Predicate.TRANSACTION, new HashMap<DataKey, Object>() {{
            put(DataKey.TRANSACTION, t);
        }});
    }

    public static Event onTransactions(List<Transaction> transactions) {
        return new Event(Predicate.TRANSACTIONS, new HashMap<DataKey, Object>() {{
            put(DataKey.TRANSACTIONS, transactions);
        }});
    }

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    public Object getData(DataKey k) {
        return data.get(k);
    }

    public Account getAccount() {
        return (Account) data.get(ACCOUNT);
    }

    public Transaction getTransaction() {
        return (Transaction) data.get(TRANSACTION);
    }

    public List<Transaction> getTransactions() {
        return (List<Transaction>) data.get(DataKey.TRANSACTIONS);
    }

    public Collection<Account> getAccounts() {
        return (Collection<Account>) data.get(ACCOUNTS);
    }
}
