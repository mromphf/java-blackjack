package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class Transactor extends EventConnection implements
        SnapshotListener,
        BetListener,
        AccountListener,
        TransactionListener,
        TransactionResponder {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;
    private final Collection<Transaction> transactions;

    public Transactor(Collection<Function<Snapshot, Optional<Transaction>>> evaluators, Collection<Transaction> transactions) {
        this.evaluationFunctions = evaluators;
        this.transactions = transactions;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        final Collection<Transaction> transactions = evaluationFunctions.stream()
            .map(f -> f.apply(snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        final Event<Collection<Transaction>> event = new Event<>(
                snapshot.getTimestamp(), TRANSACTION_SERIES, transactions);

        eventNetwork.onTransactionsEvent(event);
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final String description = "SIGNING BONUS";
            final int signingBonus = 200;
            final UUID accountKey = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED).getKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Event<Transaction> evt = new Event<>(timestamp, TRANSACTION, transaction);

            eventNetwork.onTransactionEvent(evt);
        }
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        if (event.is(BET_PLACED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final Bet bet = event.getData();
            final String description = "BET";
            final int betVal = (bet.getVal() * -1);
            final UUID accountKey = bet.getAccountKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, betVal);
            final Event<Transaction> evt = new Event<>(timestamp, TRANSACTION, transaction);

            eventNetwork.onTransactionEvent(evt);
        }
    }

    @Override
    public Collection<Transaction> requestTransactionsByKey(UUID accountKey) {
        return transactions.stream()
                .filter(t -> t.getAccountKey().equals(accountKey))
                .collect(Collectors.toList());
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTIONS_LOADED)) {
            transactions.addAll(event.getData());
        }
    }
}
