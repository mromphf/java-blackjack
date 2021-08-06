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
    private final Memory memory;

    public AccountStorage(UUID key, Memory memory) {
        this.key = key;
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Map<LocalDateTime, UUID> closures = memory.loadAllClosedAccountKeys();
        final Collection<Transaction> allTransactions = memory.loadAllTransactions(closures.values());
        final Map<UUID, List<Transaction>> grouped = allTransactions.stream()
                .collect(groupingBy(Transaction::getAccountKey));

        final Collection<Account> accounts = memory.loadAllAccounts(closures.values()).stream()
                .map(a -> applyTransactions(a, grouped))
                .collect(Collectors.toSet());

        final Event<Collection<Transaction>> transEvent = new Event<>(key, now(), TRANSACTIONS_LOADED, allTransactions);
        final Event<Collection<Account>> event = new Event<>(key, now(), ACCOUNTS_LOADED, accounts);

        eventNetwork.onAccountsEvent(event);
        eventNetwork.onTransactionsEvent(transEvent);
    }

    public void loadAllTransactions() {
       // This method and its usages can be deleted -- Aug 6 2021
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            memory.saveTransaction(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            memory.saveTransactions(event.getData());
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            memory.openAccount(event.getData());
        } else if (event.is(ACCOUNT_DELETED)) {
            memory.closeAccount(event.getData());
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
