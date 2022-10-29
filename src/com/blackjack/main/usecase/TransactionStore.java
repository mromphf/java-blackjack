package com.blackjack.main.usecase;

import com.blackjack.main.domain.function.Assessment;
import com.blackjack.main.domain.model.Account;
import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static com.blackjack.main.adapter.injection.Bindings.TRANSACTION_MAP;
import static com.blackjack.main.domain.model.Transaction.signingBonus;

public class TransactionStore implements AccountRegistrar, TableObserver {

    private final Collection<Assessment> assessments;
    private final Map<UUID, Collection<Transaction>> transactionMap;
    private final TransactionRepository transactionRepository;

    @Inject
    public TransactionStore(
            TransactionRepository transactionRepository,
            Collection<Assessment> assessors,
            @Named(TRANSACTION_MAP) Map<UUID, Collection<Transaction>> transactionMap) {
        this.transactionMap = transactionMap;
        this.assessments = assessors;
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
    public void onUpdate(TableView tableView) {
        assessments.stream()
                .map(function -> function.apply(tableView))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(groupingBy(Transaction::accountKey))
                .forEach(this::save);
    }

    @Override
    public void createNew(Account account) {
        final Transaction signingBonus = signingBonus(account);
        mapToCache(signingBonus.accountKey(), of(signingBonus).collect(toList()));
        transactionRepository.saveTransaction(signingBonus);
    }

    public Collection<Transaction> loadAll() {
        final Collection<Transaction> transactions = transactionRepository.loadAllTransactions();
        transactions.stream()
                .collect(groupingBy(Transaction::accountKey))
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
