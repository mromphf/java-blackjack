package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Round;
import main.domain.model.Account;
import main.domain.model.Action;
import main.domain.model.Deck;
import main.domain.model.Snapshot;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.abs;
import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.DECK;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.model.Action.*;

public class Game {

    private final int maxCards;

    private final Deck deck;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();
    private final Collection<SnapshotListener> snapshotListeners;
    private final SelectionService selectionService;

    @Inject
    public Game(
                SelectionService selectionService,
                Collection<SnapshotListener> snapshotListeners,
                @Named(DECK) Deck deck,
                @Named(MAX_CARDS) int maxCards) {
        this.selectionService = selectionService;
        this.deck = deck;
        this.maxCards = maxCards;
        this.snapshotListeners = snapshotListeners;
    }

    public Snapshot start() throws IllegalStateException {
        final Optional<Account> selectedAccount = selectionService.getCurrentlySelectedAccount();

        if (roundStack.size() > 0 && selectedAccount.isPresent()) {
            return roundStack.peek().getSnapshot(now(), selectedAccount.get());
        } else {
            throw new IllegalStateException();
        }
    }

    public Snapshot onActionTaken(Action action) {
        final Optional<Account> selectedAccount = selectionService.getCurrentlySelectedAccount();
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

            final Snapshot snapshot = currentRound.getSnapshot(timestamp, selectedAccount.get());
            return notifyListeners(snapshot);
        } else {
            throw new IllegalStateException();
        }
    }

    public float deckProgress() {
        return (float) (deck.size() / maxCards);
    }

    public void placeBet(int bet) {
        final Optional<Account> selectedAccount = selectionService.getCurrentlySelectedAccount();

        if (selectedAccount.isPresent()) {
            roundStack.add(new Round(deck, abs(bet)));
            notifyListeners(roundStack.peek().getSnapshot(now(), selectedAccount.get()));
        } else {
            throw new IllegalStateException();
        }
    }

    private Snapshot notifyListeners(final Snapshot snapshot) {
        for (SnapshotListener listener : snapshotListeners ) {
            listener.onGameUpdate(snapshot);
        }
        return snapshot;
    }
}
