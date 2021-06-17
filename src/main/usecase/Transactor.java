package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.usecase.eventing.Predicate.*;

public class Transactor extends EventConnection implements SnapshotListener, BetListener, AccountListener {

    private final Collection<Function<Snapshot, Optional<Transaction>>> evaluationFunctions;

    public Transactor(Collection<Function<Snapshot, Optional<Transaction>>> evaluators) {
        this.evaluationFunctions = evaluators;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        final List<Transaction> workingTransactions = evaluationFunctions.stream()
            .map(f -> f.apply(snapshot))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        final Event<List<Transaction>> event = new Event<>(TRANSACTION_SERIES, workingTransactions);

        eventNetwork.onTransactionsEvent(event);
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final String description = "SIGNING BONUS";
            final int signingBonus = 200;
            final UUID accountKey = eventNetwork.fulfill(ACCOUNT_SELECTED).getKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Event<Transaction> evt = new Event<>(TRANSACTION, transaction);

            eventNetwork.onTransactionEvent(evt);
        }
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        if (event.is(BET_PLACED)) {
            final LocalDateTime timestamp = LocalDateTime.now();
            final Bet bet = event.getData();
            final String description = "BET";
            final int signingBonus = (bet.getVal() * -1);
            final UUID accountKey = bet.getAccountKey();
            final Transaction transaction = new Transaction(timestamp, accountKey, description, signingBonus);
            final Event<Transaction> evt = new Event<>(TRANSACTION, transaction);

            eventNetwork.onTransactionEvent(evt);
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getPredicate(), account)));
    }
}
