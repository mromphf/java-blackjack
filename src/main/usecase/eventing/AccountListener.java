package main.usecase.eventing;

import main.domain.model.Account;

import java.util.Collection;

public interface AccountListener {
    default void onAccountCreated(Account account) {}
    default void onAccountDeleted(Account account) {}
    default void onAccountSelected(Account account) {}
    default void onAccountsLoaded(Collection<Account> accounts) {}
}
