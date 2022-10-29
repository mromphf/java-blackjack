package com.blackjack.main.usecase;

import com.blackjack.main.domain.function.Assessment;
import com.blackjack.main.domain.model.Account;
import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static java.util.Optional.empty;
import static com.blackjack.main.adapter.injection.Bindings.ACCOUNT_STACK;

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
                .map(assessment -> assessment.apply(tableView))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::apply);
    }

    @Override
    public void createNew(Account account) {
        accountRepository.createNew(account);
        accountMap.put(account.key(), account);
        selections.add(account.key());
    }

    public void onAccountDeleted(Account account) {
        accountRepository.closeAccount(account);
        accountMap.remove(account.key());
    }

    public void selectAccount(Account account) {
        selections.add(account.key());
    }

    public Collection<Account> loadAll() {
        final Collection<Account> accounts = accountRepository.loadAllAccounts();
        accounts.forEach(account -> accountMap.put(account.key(), account));
        return accounts;
    }

    private void apply(Transaction transaction) {
        final UUID accountKey = transaction.accountKey();
        accountMap.put(accountKey, accountMap.get(accountKey).apply(transaction));
    }
}
