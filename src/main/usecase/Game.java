package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import java.util.*;

import static main.usecase.DataKey.LAYOUT;
import static main.usecase.Event.gameStateUpdated;
import static main.usecase.Layout.HOME;
import static main.usecase.Predicate.LAYOUT_CHANGED;

public class Game extends EventConnection implements ActionListener, EventListener {

    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private Round round;

    public Game(Stack<Card> deck, int numDecks) {
        this.deck = deck;
        this.maxCards = deck.size();
        this.numDecks = numDecks;
        round = new Round(0, deck, maxCards, numDecks);
    }

    @Override
    public void onBetPlaced(int amount) {
        round = new Round(amount, deck, maxCards, numDecks);
        eventNetwork.post(gameStateUpdated(round.getSnapshot()));
    }

    @Override
    public void onActionTaken(Action action) {
        round.record(action);

        switch (action) {
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
                round.rewind();
            case DOUBLE:
                round.doubleDown();
                break;
            case BUY_INSURANCE:
            case NO_INSURANCE:
            case REFILL:
            default:
                break;
        }

        round.playNextHand();

        eventNetwork.post(gameStateUpdated(round.getSnapshot()));
    }

    @Override
    public void listen(Event e) {
        if (e.is(LAYOUT_CHANGED) && e.getData(LAYOUT).equals(HOME)) {
            round = new Round(0, deck, maxCards, numDecks);
        }
    }
}
