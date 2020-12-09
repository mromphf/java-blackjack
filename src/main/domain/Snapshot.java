package main.domain;

import java.util.Collection;
import java.util.Stack;

import static main.domain.Rules.insuranceAvailable;

public class Snapshot {
    private final int balance;
    private final int bet;
    private final boolean doubleDown;
    private final Outcome outcome;
    private final Stack<Card> deck;
    private final Collection<Card> dealerHand;
    private final Collection<Card> playerHand;
    private final Stack<Collection<Card>> handsToPlay;
    private final Stack<Snapshot> handsToSettle;
    private final Stack<Action> actionsTaken;

    public Snapshot(int balance,
                    int bet,
                    boolean doubleDown,
                    Outcome outcome,
                    Stack<Card> deck,
                    Collection<Card> dealerHand,
                    Collection<Card> playerHand,
                    Stack<Collection<Card>> handsToPlay,
                    Stack<Snapshot> handsToSettle,
                    Stack<Action> actionsTaken) {
        this.balance = balance;
        this.bet = bet;
        this.doubleDown = doubleDown;
        this.deck = deck;
        this.outcome = outcome;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.handsToPlay = handsToPlay;
        this.handsToSettle = handsToSettle;
        this.actionsTaken = actionsTaken;
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

    public boolean getDoubleDown() {
        return doubleDown;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public boolean is(Outcome outcome) {
        return this.outcome.equals(outcome);
    }

    public boolean isResolved() {
        return !outcome.equals(Outcome.UNRESOLVED);
    }

    public boolean isAtLeastOneCardDrawn() {
        return playerHand.size() > 2;
    }

    public boolean isRoundFinished() {
        return handsToSettle.isEmpty();
    }

    public boolean isInsuranceAvailable() {
        return insuranceAvailable(dealerHand) &&
                balance >= bet &&
                actionsTaken.isEmpty();
    }

    public Collection<Card> getDealerHand() {
        return dealerHand;
    }

    public Collection<Card> getPlayerHand() {
        return playerHand;
    }

    public Stack<Collection<Card>> getHandsToPlay() {
        return handsToPlay;
    }

    public Stack<Action> getActionsTaken() {
        return actionsTaken;
    }
}
