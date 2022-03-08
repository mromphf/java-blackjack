package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;

import static java.time.LocalDateTime.now;

public class AccountCache extends EventConnection implements AccountListener, TransactionListener {

    private final SortedMap<LocalDateTime, Account> selections;

    @Inject
    public AccountCache(@Named("accountMap") SortedMap<LocalDateTime, Account> selections) {
        this.selections = selections;
    }

    public Optional<Account> getCurrentlySelectedAccount() {
        if (selections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(selections.get(selections.lastKey()));
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        final Account currentState = selections.get(selections.lastKey());
        final Account updatedState = currentState.updateBalance(transaction);

        selections.put(now(), updatedState);
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        final Account currentState = selections.get(selections.lastKey());
        final Account updatedState = currentState.updateBalance(transactions);

        selections.put(now(), updatedState);
    }

    @Override
    public void onAccountCreated(Account account) {
        selections.put(now(), account);
    }

    @Override
    public void onAccountSelected(Account account) {
        selections.put(now(), account);
    }
}
