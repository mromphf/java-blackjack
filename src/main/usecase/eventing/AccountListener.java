package main.usecase.eventing;

import main.common.Identifiable;
import main.domain.Account;

import java.util.Collection;

public interface AccountListener extends Identifiable {
    void onAccountCreated(Account account);
    void onAccountDeleted(Account account);
    void onAccountSelected(Account account);
    void onAccountBalanceUpdated(Account account);
    default void onAccountsLoaded(Collection<Account> accounts) {}
}
