package main.usecase;

import main.domain.Account;
import main.usecase.eventing.AccountListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountMap implements AccountListener {

    private final Map<UUID, Account> accountMap = new HashMap<>();

    public Optional<Account> getAccountByKey(UUID accountKey) {
        if (accountMap.containsKey(accountKey)) {
            return Optional.of(accountMap.get(accountKey));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onAccountCreated(Account account) {
        throw new RuntimeException();
    }

    @Override
    public void onAccountDeleted(Account account) {
        throw new RuntimeException();
    }

    @Override
    public void onAccountSelected(Account account) {
        throw new RuntimeException();
    }
}
