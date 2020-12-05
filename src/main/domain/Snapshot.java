package main.domain;

import java.util.Collection;
import java.util.Stack;

public class Snapshot {
    private final int balance;
    private final int bet;
    private final Stack<Card> deck;
    private final Collection<Card> dealerHand;
    private final Collection<Card> playerHand;
    private final Stack<Stack<Card>> handsToPlay;
    private final Stack<Stack<Card>> handsToSettle;

    public Snapshot(int balance,
                    int bet,
                    Stack<Card> deck,
                    Collection<Card> dealerHand,
                    Collection<Card> playerHand,
                    Stack<Stack<Card>> handsToPlay,
                    Stack<Stack<Card>> handsToSettle) {
        this.balance = balance;
        this.bet = bet;
        this.deck = deck;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.handsToPlay = handsToPlay;
        this.handsToSettle = handsToSettle;
    }

    public int getBalance() {
        return balance;
    }

    public int getBet() {
        return bet;
    }

    public int getDeckSize() {
        return deck.size();
    }

   public boolean isAtLeastOneCardDrawn() {
        return playerHand.size() > 2;
    }

    public boolean isRoundFinished() {
        return handsToSettle.isEmpty();
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
