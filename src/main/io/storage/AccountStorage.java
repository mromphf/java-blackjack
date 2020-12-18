package main.io.storage;

import main.domain.Account;

import java.util.List;

public class AccountStorage {

    public static List<Account> getAll() {
        return new SaveFile().getAllAccounts();
    }
}
