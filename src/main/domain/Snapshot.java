package main.domain;

import java.util.Collection;
import java.util.Stack;

public class Snapshot {
    public final int balance;
    public final int bet;
    public final int deckSize;
    public final boolean atLeastOneCardDrawn;
    public final boolean isRoundFinished;
    public final Collection<Card> dealerHand;
    public final Collection<Card> playerHand;
    public final Stack<Stack<Card>> handsToPlay;

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
}
