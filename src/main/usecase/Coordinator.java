package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Action;
import main.domain.model.Bets;
import main.domain.model.Deck;
import main.domain.model.Table;
import main.domain.process.Round;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Properties;
import java.util.Stack;

import static java.time.LocalDateTime.now;
import static main.adapter.injection.Bindings.*;
import static main.domain.process.Round.newRound;

public class Coordinator implements Game {

    private final Deck deck;
    private final Stack<Round> roundStack = new Stack<>();
    private final Collection<TableObserver> tableObservers;
    private final Properties config;

    @Inject
    public Coordinator(Collection<TableObserver> tableObservers,
                       @Named(DECK) Deck deck,
                       @Named(MAX_CARDS) double deckSize) {

        this.deck = deck;
        this.tableObservers = tableObservers;
        this.config = new Properties();

        config.put(MAX_CARDS, deckSize);
        config.put(MIN_DEALER_SCORE, 16);
    }

    @Override
    public void bet(Bets bets) {
        roundStack.add(newRound(deck, bets, config));
        notifyObservers(roundStack.peek().getSnapshot(now()));
    }

    @Override
    public Table peek() throws IllegalStateException {
        if (roundStack.size() > 0) {
            return roundStack.peek().getSnapshot(now());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Table action(Action action) {
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

    private Table notifyObservers(final Table table) {
        for (TableObserver observer : tableObservers) {
            observer.onUpdate(table);
        }
        return table;
    }
}
