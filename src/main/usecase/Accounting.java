package main.usecase;

import main.domain.Account;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.util.Collection;

import static main.usecase.eventing.Predicate.*;

public class Accounting extends EventConnection implements Responder, EventListener, AccountListener {

    private Account account;

    public Accounting() {
        this.account = null;
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(TRANSACTION)) {
            this.account = account.updateBalance(message.getTransaction());
            eventNetwork.onBalanceUpdated();
        } else if (message.is(TRANSACTION_SERIES)) {
            this.account = account.updateBalance(message.getTransactions());
            eventNetwork.onBalanceUpdated();
        }
    }

    @Override
    public Account fulfill(Predicate predicate) {
        return account;
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            this.account = event.getData();
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {}
}
