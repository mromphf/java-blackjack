package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.*;
import main.domain.model.*;
import main.usecase.eventing.LayoutListener;
import main.usecase.eventing.SnapshotListener;
import main.usecase.eventing.TransactionListener;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.abs;
import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.DECK;
import static main.adapter.injection.Bindings.SNAPSHOT_LISTENERS;
import static main.domain.model.Action.*;
import static main.domain.function.Dealer.freshlyShuffledDeck;
import static main.usecase.Layout.HOME;

public class Game implements LayoutListener, TransactionListener {

    private final Deck deck;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();
    private final AccountService accountService;
    private final Collection<SnapshotListener> snapshotListeners;

    @Inject
    public Game(@Named(SNAPSHOT_LISTENERS) Collection<SnapshotListener> snapshotListeners,
                AccountService accountService,
                @Named(DECK) Deck deck) {
        this.accountService = accountService;
        this.deck = deck;
        this.snapshotListeners = snapshotListeners;
    }

    public void onActionTaken(Action action) {
        final Optional<Account> selectedAccount = accountService.getCurrentlySelectedAccount();
        final LocalDateTime timestamp = now();

        if (roundStack.size() > 0 && selectedAccount.isPresent()) {
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::rewind);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(PLAY_NEXT_HAND, currentRound::playNextHand);

            currentRound.record(timestamp, action);
            runnableMap.getOrDefault(action, () -> {}).run();
            notifyListeners(currentRound.getSnapshot(timestamp, selectedAccount.get()));
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        final Optional<Account> selectedAccount = accountService.getCurrentlySelectedAccount();

        if (transaction.getDescription().equals("BET") && selectedAccount.isPresent()) {
            roundStack.add(new Round(deck, abs(transaction.getAmount())));

            notifyListeners(roundStack.peek().getSnapshot(now(), selectedAccount.get()));
        }
    }

    @Override
    public void onLayoutEvent(Layout event) {
        if (event == HOME && roundStack.size() > 0) {
            deck.clear();
            deck.addAll(freshlyShuffledDeck());
        }
    }

    private void notifyListeners(final Snapshot snapshot) {
        for (SnapshotListener listener : snapshotListeners ) {
            listener.onGameUpdate(snapshot);
        }
    }
}
