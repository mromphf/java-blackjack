package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.adapter.storage.TransactionRepository;
import main.domain.Assessment;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static main.adapter.injection.Bindings.EVALUATORS;
import static main.adapter.injection.Bindings.TRANSACTION_MAP;
import static main.domain.model.Transaction.signingBonus;

public class TransactionService implements AccountRegistrar, GameObserver {

    private final Collection<Assessment> assessments;
    private final Map<UUID, Collection<Transaction>> transactionMap;
    private final TransactionRepository transactionRepository;

    @Inject
    public TransactionService(
            TransactionRepository transactionRepository,
            @Named(EVALUATORS) Collection<Assessment> evaluators,
            @Named(TRANSACTION_MAP) Map<UUID, Collection<Transaction>> transactionMap) {
        this.transactionMap = transactionMap;
        this.assessments = evaluators;
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByKey(UUID accountKey) {
        if (transactionMap.containsKey(accountKey)) {
            return new ArrayList<>(transactionMap.get(accountKey));
        } else {
            return emptyList();
        }
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        assessments.stream()
                .map(function -> function.apply(snapshot))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(groupingBy(Transaction::getAccountKey))
                .forEach(this::save);
    }

    @Override
    public void createNew(Account account) {
        final Transaction signingBonus = signingBonus(account);
        mapToCache(signingBonus.getAccountKey(), of(signingBonus).collect(toList()));
        transactionRepository.saveTransaction(signingBonus);
    }

    public Collection<Transaction> loadAll() {
        final Collection<Transaction> transactions = transactionRepository.loadAllTransactions();
        transactions.stream()
                .collect(groupingBy(Transaction::getAccountKey))
                .forEach(this::mapToCache);
        return transactions;
    }

    private void save(UUID key, Collection<Transaction> transactions) {
        mapToCache(key, transactions);
        transactionRepository.saveTransactions(transactions);
    }

    private void mapToCache(UUID key, Collection<Transaction> transactions) {
        if (transactionMap.containsKey(key)) {
            transactionMap.get(key).addAll(transactions);
        } else {
            transactionMap.put(key, transactions);
        }
    }
}
