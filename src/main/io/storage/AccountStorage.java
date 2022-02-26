package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static main.usecase.eventing.Predicate.*;


public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final UUID key;
    private final TransactionMemory transactionMemory;
    private final AccountMemory accountMemory;

    public AccountStorage(UUID key, TransactionMemory transactionMemory, AccountMemory accountMemory) {
        this.key = key;
        this.transactionMemory = transactionMemory;
        this.accountMemory = accountMemory;
    }

    public void loadAllAccounts() {
        final Map<LocalDateTime, UUID> closures = transactionMemory.loadAllClosedAccountKeys();
        final Collection<Account> accounts = accountMemory.loadAllAccounts(closures.values());

        final Collection<Transaction> allTransactions = transactionMemory.loadAllTransactions(accounts);
        final Map<UUID, List<Transaction>> grouped = allTransactions.stream()
                .collect(groupingBy(Transaction::getAccountKey));

        final Event<Collection<Transaction>> transEvent = new Event<>(key, now(), TRANSACTIONS_LOADED, allTransactions);
        final Event<Collection<Account>> event = new Event<>(key, now(), ACCOUNTS_LOADED, accounts.stream()
                .map(a -> applyTransactions(a, grouped))
                .collect(Collectors.toList()));

        eventNetwork.onAccountsEvent(event);
        eventNetwork.onTransactionsEvent(transEvent);
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            transactionMemory.saveTransaction(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            transactionMemory.saveTransactions(event.getData());
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            accountMemory.openNewAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            accountMemory.closeAccount(event.getData());
        }
    }

    private Account applyTransactions(Account account, Map<UUID, List<Transaction>> grouped) {
        final UUID accountKey = account.getKey();

        if (grouped.containsKey(accountKey)) {
            return account.updateBalance(grouped.get(accountKey));
        }

        return account;
    }
}
