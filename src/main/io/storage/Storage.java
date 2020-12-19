package main.io.storage;

import main.domain.Account;

import java.util.Set;

public interface Storage {
    Set<Account> getAllAccounts();
}
