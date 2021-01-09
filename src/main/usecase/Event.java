package main.usecase;

import main.domain.Account;

import java.util.HashMap;
import java.util.Map;

import static main.usecase.DataKey.*;
import static main.usecase.Predicate.BALANCE_UPDATED;

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

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    public Object getData(DataKey k) {
        return data.get(k);
    }
}
