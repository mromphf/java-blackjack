package main.usecase;

import main.domain.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.Event;
import main.usecase.eventing.TransactionListener;
import main.usecase.eventing.TransactionResponder;

import java.util.*;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class TransactionMemory extends EventConnection implements TransactionListener, TransactionResponder {

    public final Map<UUID, Collection<Transaction>> transactionMap;

    public TransactionMemory(Map<UUID, Collection<Transaction>> transactionMap) {
        this.transactionMap = transactionMap;
    }

    @Override
    public Collection<Transaction> requestTransactionsByKey(UUID accountKey) {
        return transactionMap.get(accountKey);
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            final UUID accountKey = event.getData().getAccountKey();
            final Collection<Transaction> coll = new LinkedList<>();

            coll.add(event.getData());
            mapToCache(accountKey, coll);
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTIONS_LOADED) || event.is(TRANSACTION_SERIES)) {
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
