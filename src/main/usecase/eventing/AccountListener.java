package main.usecase.eventing;

import main.domain.Account;

import java.util.Collection;

public interface AccountListener {
    void onAccountCreated(Account account);
    void onAccountDeleted(Account account);
    void onAccountSelected(Account account);
    void onAccountBalanceUpdated(Account account);
    default void onAccountsLoaded(Collection<Account> accounts) {}
}
