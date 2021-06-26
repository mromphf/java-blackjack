package main.usecase.eventing;

import main.domain.Identifiable;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface TransactionListener extends Identifiable {

    void onTransactionsEvent(Event<Collection<Transaction>> event);

    default void onTransactionEvent(Event<Transaction> event) {
        final Collection<Transaction> transactions = new ArrayList<>();
        final UUID sourceKey = event.getKey();
        final LocalDateTime timestamp = event.getTimestamp();
        final Predicate predicate = event.getPredicate();

        transactions.add(event.getData());

        onTransactionsEvent(new Event<>(sourceKey, timestamp, predicate, transactions));
    }
}
