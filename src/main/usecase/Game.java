package main.usecase;

import main.domain.Action;
import main.domain.Card;
import main.domain.Round;
import main.domain.Snapshot;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Game implements ControlListener, NavListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Stack<Card> deck;
    private Round round;
    private int balance = 200;

    public Game(Stack<Card> deck) {
        this.gameStateListeners = new ArrayList<>();
        this.deck = deck;
        round = new Round(0, deck);
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    @Override
    public void onStartNewRound(int bet) {
        try {
            final Map<String, Stack<Card>> openingHand = openingHand(deck);
            final Stack<Card> dealerHand = openingHand.get("dealer");
            final Stack<Card> playerHand = openingHand.get("player");

            round = new Round(bet, deck, dealerHand, playerHand);
            gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
        } catch (IllegalArgumentException ex) {
            System.out.println("Not enough cards to deal new hand! Quitting...");
            System.exit(0);
        }
    }

    @Override
    public void onActionTaken(Action action) {
        try {
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
                case DOUBLE:
                    round.doubleDown();
                    break;
                case BUY_INSURANCE:
                default:
                    break;
            }
        } catch (NoSuchElementException | EmptyStackException ex) {
            System.out.println("Ran out of cards! Quitting...");
            System.exit(1);
        }

        round.record(action);

        final Snapshot snapshot = round.getSnapshot();

        //TODO: Save history of snapshots?
        if (snapshot.isResolved()) {
            balance += settleBet(snapshot);
        }

        gameStateListeners.forEach(l -> l.onUpdate(balance, snapshot));
    }

    @Override
    public void onSettleHand() {
        //TODO: Move this rewind business up into this class?
        round.rewind();
        balance += settleBet(round.getSnapshot());
        gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
    }

    @Override
    public void onMoveToBettingTable() {
        gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
    }

    @Override
    public void onStopPlaying() {
        round = new Round(0, deck);
    }
}
