package main.usecase.eventing;

import main.domain.Account;

import java.util.Collection;

public interface AccountListener {
    void onAccountEvent(Event<Account> event);
    void onAccountsEvent(Event<Collection<Account>> event);
}
