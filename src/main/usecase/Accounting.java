package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.*;
import static main.usecase.eventing.Predicate.*;

public class Accounting extends EventConnection implements AccountResponder, AccountListener, TransactionListener {

    private final SortedMap<LocalDateTime, Account> selections;

    public Accounting(SortedMap<LocalDateTime, Account> selections) {
        this.selections = selections;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(now(), CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(now(), CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            selections.put(now(), event.getData());
            eventNetwork.onAccountEvent(new Event<>(now(), CURRENT_BALANCE, selections.get(selections.lastKey())));
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(now(), event.getPredicate(), account)));
    }

    @Override
    public Account requestSelectedAccount(Predicate predicate) {
        return selections.get(selections.lastKey());
    }
}
