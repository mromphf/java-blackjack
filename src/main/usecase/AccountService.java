package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.adapter.storage.AccountRepository;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.*;
import java.util.function.Function;

import static main.adapter.injection.Bindings.ACCOUNT_STACK;
import static main.adapter.injection.Bindings.EVALUATORS;

public class AccountService implements SelectionService, SnapshotListener  {

    private final Stack<UUID> selections;
    private final Map<UUID, Account> accountMap;
    private final AccountRepository accountRepository;
    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    @Inject
    public AccountService(AccountRepository accountRepository,
                          @Named(EVALUATORS) Collection<Function<Snapshot, Optional<Transaction>>> evaluators,
                          @Named(ACCOUNT_STACK) Stack<UUID> selections) {
        this.selections = selections;
        this.evaluationFunctions = evaluators;
        this.accountMap = new HashMap<>();
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> getCurrentlySelectedAccount() {
        final UUID peek = selections.peek();
        if (selections.size() > 0 && accountMap.containsKey(peek)) {
            return Optional.of(accountMap.get(peek));
        } else {
            return empty();
        }
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        evaluationFunctions.stream()
                .map(function -> function.apply(snapshot))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::apply);
    }

    public void onAccountCreated(Account account) {
        accountRepository.openNewAccount(account);
        accountMap.put(account.getKey(), account);
        selections.add(account.getKey());
    }

    public void onAccountDeleted(Account account) {
        accountRepository.closeAccount(account);
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
