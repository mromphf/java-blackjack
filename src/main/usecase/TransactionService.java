package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static main.adapter.injection.Bindings.TRANSACTION_MAP;

public class TransactionService extends EventConnection implements TransactionListener {

    public final Map<UUID, Collection<Transaction>> transactionMap;

    @Inject
    public TransactionService(@Named(TRANSACTION_MAP) Map<UUID, Collection<Transaction>> transactionMap) {
        this.transactionMap = transactionMap;
    }

    public List<Transaction> getTransactionsByKey(UUID accountKey) {
        if (transactionMap.containsKey(accountKey)) {
            return new ArrayList<>(transactionMap.get(accountKey));
        } else {
            return emptyList();
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        mapToCache(transaction.getAccountKey(), Stream.of(transaction).collect(toList()));
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactions.stream()
                .collect(groupingBy(Transaction::getAccountKey))
                .forEach(this::mapToCache);
    }

    @Override
    public void onTransactionsLoaded(Collection<Transaction> transactions) {
        transactions.stream()
                .collect(groupingBy(Transaction::getAccountKey))
                .forEach(this::mapToCache);
    }

    private void mapToCache(UUID key, Collection<Transaction> transactions) {
        if (transactionMap.containsKey(key)) {
            transactionMap.get(key).addAll(transactions);
        } else {
            transactionMap.put(key, transactions);
        }
    }
}
