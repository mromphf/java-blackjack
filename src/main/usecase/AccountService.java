package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.adapter.storage.AccountRepository;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.eventing.TransactionListener;

import java.util.*;

import static main.adapter.injection.Bindings.ACCOUNT_STACK;

public class AccountService implements TransactionListener {

    private final Stack<UUID> selections;
    private final Map<UUID, Account> accountMap;
    private final AccountRepository accountRepository;

    @Inject
    public AccountService(AccountRepository accountRepository,
                          @Named(ACCOUNT_STACK) Stack<UUID> selections) {
        this.selections = selections;
        this.accountMap = new HashMap<>();
        this.accountRepository = accountRepository;
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
}
