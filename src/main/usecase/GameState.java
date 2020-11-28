package main.usecase;

import main.domain.Card;

import java.util.Collection;

public class GameState {
    public final int bet;
    public final int cardsRemaining;
    public final boolean cardDrawn;
    public final boolean playerBusted;
    public final boolean isPush;
    public final boolean playerHasWon;
    public final Collection<Card> dealerHand;
    public final Collection<Card> playerHand;

    public GameState(int bet,
                     int cardsRemaining,
                     boolean cardDrawn,
                     boolean playerBusted,
                     boolean isPush,
                     boolean playerHasWon,
                     Collection<Card> dealerHand,
                     Collection<Card> playerHand) {
        this.bet = bet;
        this.cardsRemaining = cardsRemaining;
        this.cardDrawn = cardDrawn;
        this.playerBusted = playerBusted;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.isPush = isPush;
        this.playerHasWon = playerHasWon;
    }
}
