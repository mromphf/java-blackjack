package main.usecase;

import main.domain.Card;
import main.domain.Game;

import java.util.*;

import static main.domain.Deck.openingHand;

public class Round implements ControlListener, NavListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Stack<Card> deck;
    private Game game;

    public Round(Stack<Card> deck) {
        this.gameStateListeners = new ArrayList<>();
        this.deck = deck;
        game = new Game(200, 0, deck);
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
            final int currentBalance = game.getSnapshot().getBalance();

            game = new Game(currentBalance, bet, deck, dealerHand, playerHand);
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void onSplit() {
        try {
            game.split();
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (NoSuchElementException | EmptyStackException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void onHit() {
        try {
            game.hit();
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void onDouble() {
        try {
            game.doubleDown();
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (EmptyStackException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void onStand() {
        try {
            game.stand();
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (EmptyStackException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void onPurchaseInsurance() {
        game.settleInsurance();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onSettleHand() {
        game.settle();
        game.rewind();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onMoveToBettingTable() {
        game.settle();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onStopPlaying() {}
}
