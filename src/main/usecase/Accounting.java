package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.util.Collection;
import java.util.List;

import static main.usecase.eventing.Predicate.*;

public class Accounting extends EventConnection implements Responder, AccountListener, TransactionListener {

    private Account account;

    public Accounting() {
        this.account = null;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        this.account = account.updateBalance(event.getData());
        eventNetwork.onAccountEvent(new Event<>(CURRENT_BALANCE, account));
    }

    @Override
    public void onTransactionsEvent(Event<List<Transaction>> event) {
        this.account = account.updateBalance(event.getData());
        eventNetwork.onAccountEvent(new Event<>(CURRENT_BALANCE, account));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            this.account = event.getData();
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getPredicate(), account)));
    }

    @Override
    public Account fulfillSelectedAccount(Predicate predicate) {
        return account;
    }
}
