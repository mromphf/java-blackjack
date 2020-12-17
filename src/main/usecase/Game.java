package main.usecase;

import main.domain.*;

import java.util.*;

import static main.domain.Deck.openingHand;

public class Game implements ActionListener, NavListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Stack<Card> deck;
    private Round round;

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
            gameStateListeners.forEach(l -> l.onUpdate(round.getSnapshot()));
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

        final Snapshot snapshot = round.getSnapshot();
        gameStateListeners.forEach(l -> l.onUpdate(snapshot));
    }

    @Override
    public void onStopPlaying() {
        round = new Round(0, deck);
    }

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onMoveToBettingTable(Account account) {
        onMoveToBettingTable();
    }
}
