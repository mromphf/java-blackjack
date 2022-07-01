package main.domain;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Action;
import main.domain.model.Deck;
import main.domain.model.Snapshot;
import main.usecase.SnapshotListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.lang.Math.abs;
import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.DECK;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.Round.newRound;
import static main.domain.model.Action.*;

public class Game {

    private final int maxCards;

    private final Deck deck;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();
    private final Collection<SnapshotListener> snapshotListeners;

    @Inject
    public Game(Collection<SnapshotListener> snapshotListeners,
                @Named(DECK) Deck deck,
                @Named(MAX_CARDS) int maxCards) {
        this.deck = deck;
        this.maxCards = maxCards;
        this.snapshotListeners = snapshotListeners;
    }

    public Snapshot start() throws IllegalStateException {
        if (roundStack.size() > 0) {
            return roundStack.peek().getSnapshot(now());
        } else {
            throw new IllegalStateException();
        }
    }

    public Snapshot onActionTaken(Action action) {
        if (roundStack.size() > 0) {
            final LocalDateTime timestamp = now();
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::rewind);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(PLAY_NEXT_HAND, currentRound::playNextHand);

            currentRound.record(timestamp, action);
            runnableMap.getOrDefault(action, () -> {}).run();

            final Snapshot snapshot = currentRound.getSnapshot(timestamp);
            return notifyListeners(snapshot);
        } else {
            throw new IllegalStateException();
        }
    }

    public float deckProgress() {
        return (float) (deck.size() / maxCards);
    }

    public void placeBet(Account account, int bet) {
        final Round newRound = newRound(account, deck, abs(bet));

        roundStack.add(newRound);

        notifyListeners(roundStack.peek().getSnapshot(now()));
    }

    private Snapshot notifyListeners(final Snapshot snapshot) {
        for (SnapshotListener listener : snapshotListeners ) {
            listener.onGameUpdate(snapshot);
        }
        return snapshot;
    }
}
