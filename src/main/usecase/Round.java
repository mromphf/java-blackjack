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
        game.setBet(0);
        gameStateListeners.forEach(l -> l.onUpdate(game.getSnapshot()));
        if (game.outOfMoney()) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
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
        if (game.playerBusted() && game.moreHandsToPlay()) {
            game.loseBet();
            onStartNewRound();
        } else if (game.playerBusted()) {
            game.loseBet();
            settleHand(Outcome.BUST);
        }
    }

    @Override
    public void onDealerTurn() {
        while (game.dealerShouldHit()) {
            game.addCardToDealerHand();
        }

        settleHand(game.determineOutcome());
    }

    @Override
    public void onDouble() {
        game.doubleBet();
        onHit();
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
        if (game.moreHandsToSettle()) {
            game.rewind();
        }
        settleHand(game.determineOutcome());
    }

    @Override
    public void onStopPlaying() {}

    private void settleHand(Outcome outcome) {
        game.settle(outcome);
        switch(outcome) {
            case WIN:
                outcomeListeners.forEach(l -> l.onPlayerWins(game.getSnapshot()));
                break;
            case PUSH:
                outcomeListeners.forEach(l -> l.onPush(game.getSnapshot()));
                break;
            case BUST:
                outcomeListeners.forEach(l -> l.onBust(game.getSnapshot()));
                break;
            default:
                outcomeListeners.forEach(l -> l.onDealerWins(game.getSnapshot()));
                break;
        }
    }
}
