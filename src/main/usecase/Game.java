package main.usecase;

import main.domain.Card;
import main.domain.Round;

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
    public void onSplit() {
        try {
            round.split();
            gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
        } catch (NoSuchElementException | EmptyStackException ex) {
            System.out.println("Ran out of cards! Quitting...");
            System.exit(1);
        }
    }

    @Override
    public void onHit() {
        try {
            round.hit();
            gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
        } catch (EmptyStackException ex) {
            System.out.println("Ran out of cards! Quitting...");
            System.exit(1);
        }
    }

    @Override
    public void onDouble() {
        try {
            round.doubleDown();
            gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
        } catch (EmptyStackException ex) {
            System.out.println("Ran out of cards! Quitting...");
            System.exit(1);
        }
    }

    @Override
    public void onStand() {
        try {
            round.stand();
            gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
        } catch (EmptyStackException ex) {
            System.out.println("Ran out of cards! Quitting...");
            System.exit(1);
        }
    }

    @Override
    public void onPurchaseInsurance() {
        round.settleInsurance();
        gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
    }

    @Override
    public void onSettleHand() {
        balance += settleBet(round.getSnapshot());
        round.rewind();
        gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
    }

    @Override
    public void onMoveToBettingTable() {
        balance += settleBet(round.getSnapshot());
        gameStateListeners.forEach(l -> l.onUpdate(balance, round.getSnapshot()));
    }

    @Override
    public void onStopPlaying() {
        round = new Round(0, deck);
    }
}
