package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import java.util.*;

import static main.usecase.Layout.HOME;

public class Game extends EventConnection implements ActionListener, NavListener {

    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private Round round;

    public Game(Stack<Card> deck, int numDecks) {
        this.deck = deck;
        this.maxCards = deck.size();
        this.numDecks = numDecks;
        round = new Round(Account.placeholder(),0, new Stack<>(), maxCards, numDecks);
    }

    @Override
    public void onBetPlaced(Account account, int amount) {
        round = new Round(account, amount, deck, maxCards, numDecks);
        eventNetwork.onUpdate(round.getSnapshot());
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
            case PLAY_NEXT_HAND:
                round.playNextHand();
                break;
            case BUY_INSURANCE:
            case WAIVE_INSURANCE:
            case REFILL:
            default:
                break;
        }

        eventNetwork.onUpdate(round.getSnapshot());
    }

    @Override
    public void onChangeLayout(Layout layout) {
        if (layout == HOME) {
            round = new Round(Account.placeholder(), 0, deck, maxCards, numDecks);
        }
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        onChangeLayout(layout);
    }
}
