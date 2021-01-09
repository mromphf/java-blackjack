package main.usecase;

import main.domain.*;
import main.io.EventListener;

import java.util.*;

import static main.usecase.Layout.HOME;
import static main.domain.Deck.openingHand;

public class Game extends EventListener implements ActionListener, NavListener {

    private final Stack<Card> deck;
    private final int maxCards;
    private Round round;

    public Game(Stack<Card> deck) {
        this.deck = deck;
        this.maxCards = deck.size();
        round = new Round(0, deck, maxCards);
    }

    @Override
    public void onBetPlaced(int amount) {
        try {
            final Map<String, Stack<Card>> openingHand = openingHand(deck);
            final Stack<Card> dealerHand = openingHand.get("dealer");
            final Stack<Card> playerHand = openingHand.get("player");

            round = new Round(amount, deck, dealerHand, playerHand, maxCards);
            eventNetwork.onUpdate(round.getSnapshot());
        } catch (IllegalArgumentException ex) {
            System.out.println("Not enough cards to deal new hand! Quitting...");
            System.exit(0);
        }
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

        eventNetwork.onUpdate(round.getSnapshot());
    }

    @Override
    public void onChangeLayout(Layout layout) {
        if (layout == HOME) {
            round = new Round(0, deck, maxCards);
        }
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        onChangeLayout(layout);
    }
}
