package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Optional;
import java.util.Stack;

public class AccountCache extends EventConnection implements AccountListener, TransactionListener {

    private final Stack<Account> selections;

    @Inject
    public AccountCache(@Named("accountStack") Stack<Account> selections) {
        this.selections = selections;
    }

    public Optional<Account> getCurrentlySelectedAccount() {
        if (selections.empty()) {
            return Optional.empty();
        } else {
            return Optional.of(selections.peek());
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        try {
            selections.add(selections.pop().updateBalance(transaction));
        } catch (EmptyStackException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        try {
            selections.add(selections.pop().updateBalance(transactions));
        } catch (EmptyStackException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAccountCreated(Account account) {
        selections.add(account);
    }

    @Override
    public void onAccountSelected(Account account) {
        selections.add(account);
    }
}
