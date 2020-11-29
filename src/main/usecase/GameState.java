package main.usecase;

import main.domain.Card;

import java.util.Collection;

public class GameState {
    public final int bet;
    public final int cardsRemaining;
    public final boolean atLeastOneCardDrawn;
    public final Collection<Card> dealerHand;
    public final Collection<Card> playerHand;

    public GameState(int bet,
                     int cardsRemaining,
                     boolean atLeastOneCardDrawn,
                     Collection<Card> dealerHand,
                     Collection<Card> playerHand) {
        this.bet = bet;
        this.cardsRemaining = cardsRemaining;
        this.atLeastOneCardDrawn = atLeastOneCardDrawn;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
    }
}
