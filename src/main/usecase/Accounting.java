package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.util.*;

import static main.usecase.eventing.Predicate.*;

public class Accounting extends EventConnection implements Responder, AccountListener, TransactionListener {

    private final Stack<Account> selections = new Stack<>();

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            final Account currentState = selections.peek();
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.add(updatedState);
            eventNetwork.onAccountEvent(new Event<>(CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onTransactionsEvent(Event<List<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            final Account currentState = selections.peek();
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.add(updatedState);
            eventNetwork.onAccountEvent(new Event<>(CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            selections.add(event.getData());
            eventNetwork.onAccountEvent(new Event<>(CURRENT_BALANCE, selections.peek()));
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getPredicate(), account)));
    }

    @Override
    public Account fulfillSelectedAccount(Predicate predicate) {
        return selections.peek();
    }
}
