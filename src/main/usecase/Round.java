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
        game = new Game(200, 0, deck, new Stack<>(), new Stack<>());
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    @Override
    public void onStartNewRound(int bet) {
        try {
            final Map<String, Stack<Card>> openingHand = openingHand(deck);
            game = new Game(game.getSnapshot().getBalance(), bet, deck, openingHand.get("dealer"), openingHand.get("player"));
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        } catch (IllegalArgumentException ex) {
            System.out.println("Not enough cards to deal new hand! Quitting...");
            System.exit(0);
        }
    }

    @Override
    public void onMoveToBettingTable() {
        game.settle();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onSplit() {
        game.split();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onHit() {
        game.hit();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onDouble() {
        game.doubleDown();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onStand() {
        game.stand();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onSettleHand() {
        game.settle();
        game.rewind();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onStopPlaying() {}
}
