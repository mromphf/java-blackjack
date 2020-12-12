package main.domain;

import java.util.Collection;
import java.util.Stack;

import static main.domain.Rules.insuranceAvailable;

public class Snapshot {
    private final int bet;
    private final Outcome outcome;
    private final Stack<Card> deck = new Stack<>();
    private final Stack<Card> dealerHand = new Stack<>();
    private final Stack<Card> playerHand = new Stack<>();
    private final Stack<Stack<Card>> handsToPlay = new Stack<>();
    private final Stack<Snapshot> handsToSettle = new Stack<>();
    private final Stack<Action> actionsTaken = new Stack<>();

    public Snapshot(int bet,
                    Outcome outcome,
                    Stack<Card> deck,
                    Stack<Card> dealerHand,
                    Stack<Card> playerHand,
                    Stack<Stack<Card>> handsToPlay,
                    Stack<Snapshot> handsToSettle,
                    Stack<Action> actionsTaken) {
        this.bet = bet;
        this.outcome = outcome;
        this.deck.addAll(deck);
        this.dealerHand.addAll(dealerHand);
        this.playerHand.addAll(playerHand);
        this.handsToPlay.addAll(handsToPlay);
        this.handsToSettle.addAll(handsToSettle);
        this.actionsTaken.addAll(actionsTaken);
    }

    public int getBet() {
        return bet;
    }

    public int getDeckSize() {
        return deck.size();
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
        return insuranceAvailable(dealerHand) && actionsTaken.isEmpty();
    }

    public Collection<Card> getDealerHand() {
        return dealerHand;
    }

    public Stack<Card> getPlayerHand() {
        return playerHand;
    }

    public Stack<Stack<Card>> getHandsToPlay() {
        return handsToPlay;
    }

    public Stack<Action> getActionsTaken() {
        return actionsTaken;
    }

    public Stack<Snapshot> getHandsToSettle() {
        return handsToSettle;
    }
}
