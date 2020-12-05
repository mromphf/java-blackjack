package main.domain;

import java.util.Collection;
import java.util.Stack;

public class Snapshot {
    private final int balance;
    private final int bet;
    private final int deckSize;
    private final boolean atLeastOneCardDrawn;
    private final boolean isRoundFinished;
    private final Collection<Card> dealerHand;
    private final Collection<Card> playerHand;
    private final Stack<Stack<Card>> handsToPlay;

    public Snapshot(int balance,
                    int bet,
                    int deckSize,
                    boolean atLeastOneCardDrawn,
                    boolean isRoundFinished,
                    Collection<Card> dealerHand,
                    Collection<Card> playerHand,
                    Stack<Stack <Card>> handsToPlay) {
        this.balance = balance;
        this.bet = bet;
        this.deckSize = deckSize;
        this.atLeastOneCardDrawn = atLeastOneCardDrawn;
        this.isRoundFinished = isRoundFinished;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.handsToPlay = handsToPlay;
    }

    public int getBalance() {
        return balance;
    }

    public int getBet() {
        return bet;
    }

    public int getDeckSize() {
        return deckSize;
    }

    public boolean isAtLeastOneCardDrawn() {
        return atLeastOneCardDrawn;
    }

    public boolean isRoundFinished() {
        return isRoundFinished;
    }

    public Collection<Card> getDealerHand() {
        return dealerHand;
    }

    public Collection<Card> getPlayerHand() {
        return playerHand;
    }

    public Stack<Stack<Card>> getHandsToPlay() {
        return handsToPlay;
    }
}
