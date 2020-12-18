package main.io.storage;

import main.domain.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountStorage {

    public static List<Account> getAll() {
        return new ArrayList<Account>() {{
            add(new Account(UUID.randomUUID(), "StickyJibs", 200));
            add(new Account(UUID.randomUUID(), "Fast Browns", 100));
            add(new Account(UUID.randomUUID(), "Sam Uncles", 450));
        }};
    }
}
