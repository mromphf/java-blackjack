package main.usecase;

import main.domain.Account;

import java.util.HashMap;
import java.util.Map;

import static main.usecase.DataKey.*;
import static main.usecase.Predicate.BALANCE_UPDATED;
import static main.usecase.Predicate.LAYOUT_CHANGED;

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
