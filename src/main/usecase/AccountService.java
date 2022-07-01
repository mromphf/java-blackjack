package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.adapter.storage.AccountRepository;
import main.domain.Assessment;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.*;

import static java.util.Optional.empty;
import static main.adapter.injection.Bindings.ACCOUNT_STACK;
import static main.adapter.injection.Bindings.EVALUATORS;

public class AccountService implements SelectionService, AccountRegistrar, SnapshotListener  {

    private final Stack<UUID> selections;
    private final Map<UUID, Account> accountMap;
    private final AccountRepository accountRepository;
    private final Collection<Assessment> assessments;

    @Inject
    public AccountService(AccountRepository accountRepository,
                          @Named(EVALUATORS) Collection<Assessment> evaluators,
                          @Named(ACCOUNT_STACK) Stack<UUID> selections) {
        this.selections = selections;
        this.assessments = evaluators;
        this.accountMap = new HashMap<>();
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> getCurrentlySelectedAccount() {
        if (selections.size() > 0 && accountMap.containsKey(selections.peek())) {
            return Optional.of(accountMap.get(selections.peek()));
        } else {
            return empty();
        }
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        assessments.stream()
                .map(function -> function.apply(snapshot))
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
