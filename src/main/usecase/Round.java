package main.usecase;

import main.domain.Game;

import java.util.*;

public class Round implements ControlListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Collection<OutcomeListener> outcomeListeners;
    private final Game game;

    public Round(Game game) {
        this.gameStateListeners = new ArrayList<>();
        this.outcomeListeners = new ArrayList<>();
        this.game = game;
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    public void registerOutcomeListener(OutcomeListener outcomeListener) {
        outcomeListeners.add(outcomeListener);
    }

    @Override
    public void onStartNewRound() {
        if (game.moreHandsToPlay()) {
            game.playNextHand();
        } else {
            game.dealOpeningHand();
        }

        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onBetPlaced(int bet) {
        game.setBet(bet);
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onMoveToBettingTable() {
        game.settle(game.determineOutcome());
        game.setBet(0);

        if (game.getSnapshot().getBalance() <= 0) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
        } else {
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        }
    }

    @Override
    public void onSplit() {
        game.split();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onHit() {
        game.addCardToPlayerHand();
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));

        if (game.playerHasBusted()) {
            onStand();
        }
    }

    @Override
    public void onDealerTurn() {
        while (game.dealerShouldHit()) {
            game.addCardToDealerHand();
        }

        gameStateListeners.forEach(l -> l.onUpdate(game.getResolvedSnapshot()));
    }

    @Override
    public void onDouble() {
        game.doubleBet();
        game.addCardToPlayerHand();
        onStand();
    }

    @Override
    public void onStand() {
        if (game.moreHandsToPlay()) {
            onStartNewRound();
        } else {
            onDealerTurn();
        }
    }

    @Override
    public void onSettleHand() {
        game.settle(game.determineOutcome());

        if (!game.getSnapshot().isRoundFinished()) {
            game.rewind();
        }

        gameStateListeners.forEach(l -> l.onUpdate(game.getResolvedSnapshot()));
    }

    @Override
    public void onStopPlaying() {}
}
