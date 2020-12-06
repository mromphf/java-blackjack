package main.usecase;

import main.domain.Game;

import java.util.*;

public class Round implements ControlListener {

    private final Collection<GameStateListener> gameStateListeners;
    private final Game game;

    public Round(Game game) {
        this.gameStateListeners = new ArrayList<>();
        this.game = game;
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    @Override
    public void onStartNewRound(int bet) {
        game.setBet(bet);

        if (game.moreHandsToPlay()) {
            game.playNextHand();
        } else {
            game.dealOpeningHand();
        }

        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
    }

    @Override
    public void onMoveToBettingTable() {
        game.settle(game.determineOutcome());

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

        if (game.playerHasBusted()) {
            onStand();
        } else {
            gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        }
    }

    @Override
    public void onDouble() {
        // TODO: Bug - this will double the bet for all unsettled hands
        game.doubleBet();
        game.addCardToPlayerHand();
        onStand();
    }

    @Override
    public void onStand() {
        if (game.moreHandsToPlay()) {
            onStartNewRound(game.getSnapshot().getBet());
        } else {
            while (game.dealerShouldHit()) {
                game.addCardToDealerHand();
            }

            gameStateListeners.forEach(l -> l.onUpdate(game.getResolvedSnapshot()));
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
