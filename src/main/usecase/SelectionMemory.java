package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static main.usecase.eventing.Predicate.*;

public class SelectionMemory extends EventConnection implements AccountResponder, AccountListener, TransactionListener {

    private final UUID networkId;
    private final SortedMap<LocalDateTime, Account> selections;

    public SelectionMemory(UUID networkId, SortedMap<LocalDateTime, Account> selections) {
        this.networkId = networkId;
        this.selections = selections;
    }

    @Override
    public UUID getKey() {
        return networkId;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            final Account currentState = selections.get(selections.lastKey());
            final Account updatedState = currentState.updateBalance(event.getData());

            selections.put(now(), updatedState);
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE, updatedState));
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED) || event.is(ACCOUNT_SELECTED)) {
            selections.put(now(), event.getData());
            eventNetwork.onAccountEvent(new Event<>(networkId, now(), CURRENT_BALANCE, selections.get(selections.lastKey())));
        }
    }

    @Override
    public Optional<Account> requestSelectedAccount(Predicate predicate) {
        if (selections.size() > 0) {
            return Optional.of(selections.get(selections.lastKey()));
        }

        return Optional.empty();
    }
}
