package main.usecase.eventing;

import main.domain.Account;

import java.util.Collection;

import static java.time.LocalDateTime.*;

public interface AccountListener {

    void onAccountEvent(Event<Account> event);

    default void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(now(), event.getPredicate(), account)));
    }
}
