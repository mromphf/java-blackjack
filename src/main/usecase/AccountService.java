package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.*;

import static main.adapter.injection.Bindings.ACCOUNT_STACK;

public class AccountService extends EventConnection implements AccountListener, TransactionListener {

    private final Stack<UUID> selections;
    private final Map<UUID, Account> accountMap;

    @Inject
    public AccountService(@Named(ACCOUNT_STACK) Stack<UUID> selections) {
        this.selections = selections;
        this.accountMap = new HashMap<>();
    }

    public Optional<Account> getCurrentlySelectedAccount() {
        if (selections.size() > 0 && accountMap.containsKey(selections.peek())) {
            return Optional.of(accountMap.get(selections.peek()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        final UUID accountKey = transaction.getAccountKey();
        if (accountMap.containsKey(accountKey)) {
            accountMap.put(accountKey, accountMap.get(accountKey).apply(transaction));
        }
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactions.forEach(this::onTransactionIssued);
    }

    @Override
    public void onAccountCreated(Account account) {
        selections.add(account.getKey());
    }

    @Override
    public void onAccountSelected(Account account) {
        selections.add(account.getKey());
    }

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {
        accounts.forEach(act -> accountMap.put(act.getKey(), act));
    }
}
