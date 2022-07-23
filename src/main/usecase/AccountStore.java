package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.adapter.storage.AccountRepository;
import main.domain.function.Assessment;
import main.domain.model.Account;
import main.domain.model.TableView;
import main.domain.model.Transaction;

import java.util.*;

import static java.util.Optional.empty;
import static main.adapter.injection.Bindings.ACCOUNT_STACK;

public class AccountStore implements SelectionService, AccountRegistrar, TableObserver {

    private final Stack<UUID> selections;
    private final Map<UUID, Account> accountMap;
    private final AccountRepository accountRepository;
    private final Collection<Assessment> assessments;

    @Inject
    public AccountStore(AccountRepository accountRepository,
                        Collection<Assessment> assessors,
                        @Named(ACCOUNT_STACK) Stack<UUID> selections) {
        this.selections = selections;
        this.assessments = assessors;
        this.accountMap = new HashMap<>();
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> selectedAccount() {
        if (selections.size() > 0 && accountMap.containsKey(selections.peek())) {
            return Optional.of(accountMap.get(selections.peek()));
        } else {
            return empty();
        }
    }

    @Override
    public void onUpdate(TableView tableView) {
        assessments.stream()
                .map(function -> function.apply(tableView))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::apply);
    }

    @Override
    public void createNew(Account account) {
        accountRepository.createNew(account);
        accountMap.put(account.getKey(), account);
        selections.add(account.getKey());
    }

    public void onAccountDeleted(Account account) {
        accountRepository.closeAccount(account);
        accountMap.remove(account.getKey());
    }

    public void onAccountSelected(Account account) {
        selections.add(account.getKey());
    }

    public Collection<Account> loadAll() {
        final Collection<Account> accounts = accountRepository.loadAllAccounts();
        accounts.forEach(account -> accountMap.put(account.getKey(), account));
        return accounts;
    }

    private void apply(Transaction transaction) {
        final UUID accountKey = transaction.getAccountKey();
        if (accountMap.containsKey(accountKey)) {
            accountMap.put(accountKey, accountMap.get(accountKey).apply(transaction));
        }
    }
}
