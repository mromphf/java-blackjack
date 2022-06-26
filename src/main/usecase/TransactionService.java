package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;
import main.usecase.eventing.TransactionListener;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static main.adapter.injection.Bindings.EVALUATORS;
import static main.adapter.injection.Bindings.TRANSACTION_MAP;

public class TransactionService extends EventConnection implements TransactionListener, SnapshotListener {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;
    public final Map<UUID, Collection<Transaction>> transactionMap;

    @Inject
    public TransactionService(
            @Named(EVALUATORS) Collection<Function<Snapshot, Optional<Transaction>>> evaluators,
            @Named(TRANSACTION_MAP) Map<UUID, Collection<Transaction>> transactionMap) {
        this.transactionMap = transactionMap;
        this.evaluationFunctions = evaluators;
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

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        final Collection<Transaction> workingTransactions = evaluationFunctions.stream()
                .map(f -> f.apply(snapshot))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        workingTransactions.stream()
                .collect(groupingBy(Transaction::getAccountKey))
                .forEach(this::mapToCache);

        if (workingTransactions.size() > 0) {
            eventNetwork.onTransactionSeriesIssued(workingTransactions);
        }
    }
}
