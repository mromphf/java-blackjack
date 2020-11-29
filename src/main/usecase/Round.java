package main.usecase;

import main.domain.GameState;

import java.util.*;

public class Round implements ControlListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Collection<OutcomeListener> outcomeListeners;
    private final GameState gameState;

    public Round(GameState gameState) {
        this.gameStateListeners = new ArrayList<>();
        this.outcomeListeners = new ArrayList<>();
        this.gameState = gameState;
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    public void registerOutcomeListener(OutcomeListener outcomeListener) {
        outcomeListeners.add(outcomeListener);
    }

    @Override
    public void onStartNewRound() {
        gameState.dealOpeningHand();
        gameStateListeners.forEach(l -> l.onUpdate(gameState));
    }

    @Override
    public void onBetPlaced(int bet) {
        gameState.setBet(bet);
        gameStateListeners.forEach(l -> l.onUpdate(gameState));
    }

    @Override
    public void onMoveToBettingTable() {
        gameState.setBet(0);
        gameStateListeners.forEach(l -> l.onUpdate(gameState));
        if (gameState.outOfMoney()) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
        }
    }

    @Override
    public void onHit() {
        gameState.addCardToPlayerHand();
        gameStateListeners.forEach(l -> l.onUpdate(gameState));
        if (gameState.playerBusted()) {
            gameState.loseBet();
            outcomeListeners.forEach(l -> l.onBust(gameState));
        }
    }

    @Override
    public void onDealerTurn() {
        while (gameState.dealerShouldHit()) {
            gameState.addCardToDealerHand();
        }

        switch(gameState.determineOutcome()) {
            case WIN:
                outcomeListeners.forEach(l -> l.onPlayerWins(gameState));
                break;
            case PUSH:
                outcomeListeners.forEach(l -> l.onPush(gameState));
                break;
            case BUST:
                outcomeListeners.forEach(l -> l.onBust(gameState));
                break;
            default:
                outcomeListeners.forEach(l -> l.onDealerWins(gameState));
                break;
        }
    }

    @Override
    public void onDouble() {
        gameState.doubleBet();
        onHit();
        onDealerTurn();
    }

    @Override
    public void onStopPlaying() {}
}
