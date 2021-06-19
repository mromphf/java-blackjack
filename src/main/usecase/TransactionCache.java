package main.usecase;

import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.eventing.Event;
import main.usecase.eventing.TransactionListener;
import main.usecase.eventing.TransactionResponder;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class TransactionCache extends EventConnection implements TransactionListener, TransactionResponder {

    public final Collection<Transaction> transactions;

    public TransactionCache(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Collection<Transaction> requestTransactionsByKey(UUID accountKey) {
        return transactions.stream()
                .filter(t -> t.getAccountKey().equals(accountKey))
                .collect(Collectors.toList());
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            transactions.add(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTIONS_LOADED)) {
            transactions.addAll(event.getData());
        } else if (event.is(TRANSACTION_SERIES)) {
            transactions.addAll(event.getData());
        }
    }
}
