package main.usecase.eventing;

import main.domain.Account;
import main.common.Identifiable;

import java.util.Collection;
import java.util.LinkedList;

import static java.time.LocalDateTime.*;

public interface AccountListener extends Identifiable {

    void onAccountEvent(Event<Account> event);

    default void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getKey(), now(), event.getPredicate(), account)));
    }

    default void onAccountsLoaded(Collection<Account> accounts) {};
}
