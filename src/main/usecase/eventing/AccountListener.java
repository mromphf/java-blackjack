package main.usecase.eventing;

import main.common.Identifiable;
import main.domain.Account;

import java.util.Collection;

import static java.time.LocalDateTime.now;

public interface AccountListener extends Identifiable {

    void onAccountCreated(Account account);
    void onAccountDeleted(Account account);
    void onAccountSelected(Account account);
    void onAccountEvent(Event<Account> event);

    default void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getKey(), now(), event.getPredicate(), account)));
    }

    default void onAccountsLoaded(Collection<Account> accounts) {}
}
