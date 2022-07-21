package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Round;
import main.domain.model.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.DECK;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.Round.newRound;
import static main.domain.model.Action.*;

public class Game {

    private final float maxCards;

    private final Deck deck;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();
    private final Collection<TableObserver> tableObservers;

    @Inject
    public Game(Collection<TableObserver> tableObservers,
                @Named(DECK) Deck deck,
                @Named(MAX_CARDS) int maxCards) {
        this.deck = deck;
        this.maxCards = maxCards;
        this.tableObservers = tableObservers;
    }

    public TableView peek() throws IllegalStateException {
        if (roundStack.size() > 0) {
            return roundStack.peek().getSnapshot(now());
        } else {
            throw new IllegalStateException();
        }
    }

    public TableView onActionTaken(Action action) {
        if (roundStack.size() > 0) {
            final LocalDateTime timestamp = now();
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::settleNextHand);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(NEXT, currentRound::playNextHand);

            currentRound.record(timestamp, action);
            runnableMap.getOrDefault(action, () -> {}).run();

            final TableView tableView = currentRound.getSnapshot(timestamp);
            return notifyObservers(tableView);
        } else {
            throw new IllegalStateException();
        }
    }

    public double deckProgress() {
        return deck.size() / maxCards;
    }

    public void placeBet(Account account, int bet) {
        final Bets bets = new Bets();

        bets.put(account, bet);

        final Round newRound = newRound(account, deck, bets);

        roundStack.add(newRound);

        notifyObservers(roundStack.peek().getSnapshot(now()));
    }

    private TableView notifyObservers(final TableView tableView) {
        for (TableObserver observer : tableObservers) {
            observer.onUpdate(tableView);
        }
        return tableView;
    }
}
