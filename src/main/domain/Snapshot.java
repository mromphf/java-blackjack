package main.domain;

import java.util.Collection;

public class Snapshot {
    public final int balance;
    public final int bet;
    public final int deckSize;
    public final boolean atLeastOneCardDrawn;
    public final Collection<Card> dealerHand;
    public final Collection<Card> playerHand;

    public Snapshot(int balance, int bet, int deckSize, boolean atLeastOneCardDrawn, Collection<Card> dealerHand, Collection<Card> playerHand) {
        this.balance = balance;
        this.bet = bet;
        this.deckSize = deckSize;
        this.atLeastOneCardDrawn = atLeastOneCardDrawn;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
    }
}
