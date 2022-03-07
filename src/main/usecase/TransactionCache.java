package main.usecase;

import com.google.inject.Inject;
import main.domain.Transaction;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.*;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class TransactionCache extends EventConnection implements TransactionListener {

    public final UUID key;
    public final Map<UUID, Collection<Transaction>> transactionMap;

    @Inject
    public TransactionCache(UUID key, Map<UUID, Collection<Transaction>> transactionMap) {
        this.key = key;
        this.transactionMap = transactionMap;
    }

    public List<Transaction> getTransactionsByKey(UUID accountKey) {
        if (transactionMap.containsKey(accountKey)) {
            return new ArrayList<>(transactionMap.get(accountKey));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION_ISSUED)) {
            final UUID accountKey = event.getData().getAccountKey();
            final Collection<Transaction> coll = new LinkedList<>();

            coll.add(event.getData());
            mapToCache(accountKey, coll);
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTIONS_LOADED) || event.is(TRANSACTION_SERIES_ISSUED)) {
            final Map<UUID, List<Transaction>> grouped = event.getData()
                    .stream()
                    .collect(Collectors.groupingBy(Transaction::getAccountKey));

            grouped.forEach(this::mapToCache);
        }
    }

    private void mapToCache(UUID accountKey, Collection<Transaction> transactions) {
        if (transactionMap.containsKey(accountKey)) {
            transactionMap.get(accountKey).addAll(transactions);
        } else {
            transactionMap.put(accountKey, transactions);
        }
    }
}
