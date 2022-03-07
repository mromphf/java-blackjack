package main.usecase;

import main.domain.Account;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.Predicate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class AccountMap implements AccountListener {

    private final UUID networkKey = randomUUID();
    private final Map<UUID, Account> accountMap = new HashMap<>();

    public Optional<Account> getAccountByKey(UUID accountKey) {
        if (accountMap.containsKey(accountKey)) {
            return Optional.of(accountMap.get(accountKey));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public UUID getKey() {
        return networkKey;
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(Predicate.ACCOUNT_SELECTED)) {
            final Account account = event.getData();

            accountMap.put(account.getKey(), account);
        }
    }
}
