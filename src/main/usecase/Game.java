package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.process.Round;
import main.domain.model.Action;
import main.domain.model.Bets;
import main.domain.model.Deck;
import main.domain.model.TableView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Stack;

import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.DECK;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.process.Round.newRound;

public class Game implements GameListener {

    private final float maxCards;

    private final Deck deck;
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

    public void placeBets(Bets bets) {
        roundStack.add(newRound(deck, bets));
        notifyObservers(roundStack.peek().getSnapshot(now()));
    }

    @Override
    public TableView peek() throws IllegalStateException {
        if (roundStack.size() > 0) {
            return roundStack.peek().getSnapshot(now());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public TableView onAction(Action action) {
        if (roundStack.size() > 0) {
            final LocalDateTime timestamp = now();
            final Round round = roundStack.peek();

            round.record(timestamp, action);

            switch(action) {
                case HIT:
                    round.hit();
                    break;
                case SPLIT:
                    round.split();
                    break;
                case STAND:
                    round.stand();
                    break;
                case SETTLE:
                    round.settleNextHand();
                    break;
                case DOUBLE:
                    round.doubleDown();
                    break;
                case NEXT:
                    round.playNextHand();
                    break;
                default:
                    break;
            }

            return notifyObservers(round.getSnapshot(timestamp));

        } else {
            throw new IllegalStateException();
        }
    }

    public double deckProgress() {
        return deck.size() / maxCards;
    }

    private TableView notifyObservers(final TableView tableView) {
        for (TableObserver observer : tableObservers) {
            observer.onUpdate(tableView);
        }
        return tableView;
    }
}
