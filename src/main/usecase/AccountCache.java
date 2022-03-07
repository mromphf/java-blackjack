package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.usecase.eventing.Predicate.*;

public class AccountCache extends EventConnection implements AccountListener, TransactionListener {

    private final UUID networkId = randomUUID();
    private final SortedMap<LocalDateTime, Account> selections = new TreeMap<>();

    public Optional<Account> getLastSelectedAccount() {
        if (selections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(selections.get(selections.lastKey()));
        }
    }

    @Override
    public UUID getKey() {
        return networkId;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION_ISSUED)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE_UPDATED, updatedState));
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES_ISSUED)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE_UPDATED, updatedState));
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            selections.put(now(), event.getData());
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE_UPDATED, selections.get(selections.lastKey())));
        }
    }
}
