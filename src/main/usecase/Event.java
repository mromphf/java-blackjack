package main.usecase;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;

import java.util.HashMap;
import java.util.Map;

import static main.usecase.DataKey.*;
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

    public static Event layoutChanged(Layout layout, Account account) {
        return new Event(LAYOUT_CHANGED, new HashMap<DataKey, Object>() {{
            put(LAYOUT, layout);
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

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    public Object getData(DataKey k) {
        return data.get(k);
    }

    public boolean hasData(DataKey k) {
        return data.containsKey(k);
    }
}
