package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Transaction;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class TransactionCache extends EventConnection implements TransactionListener {

    public final Map<UUID, Collection<Transaction>> transactionMap;

    @Inject
    public TransactionCache(@Named("transactionMap") Map<UUID, Collection<Transaction>> transactionMap) {
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
    public void onTransactionIssued(Transaction transaction) {
        final UUID accountKey = transaction.getAccountKey();
        final Collection<Transaction> coll = new LinkedList<>();

        coll.add(transaction);
        mapToCache(accountKey, coll);
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        final Map<UUID, List<Transaction>> grouped = transactions.stream()
                .collect(groupingBy(Transaction::getAccountKey));

        grouped.forEach(this::mapToCache);
    }

    @Override
    public void onTransactionsLoaded(Collection<Transaction> transactions) {
        final Map<UUID, List<Transaction>> grouped = transactions.stream()
                .collect(groupingBy(Transaction::getAccountKey));

        grouped.forEach(this::mapToCache);
    }

    private void mapToCache(UUID accountKey, Collection<Transaction> transactions) {
        if (transactionMap.containsKey(accountKey)) {
            transactionMap.get(accountKey).addAll(transactions);
        } else {
            transactionMap.put(accountKey, transactions);
        }
    }
}
