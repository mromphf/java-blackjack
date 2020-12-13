package main.domain;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Stack;

import static main.StringUtil.concat;
import static main.domain.Rules.*;

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
                    Stack<Card> deck,
                    Stack<Card> dealerHand,
                    Stack<Card> playerHand,
                    Stack<Stack<Card>> handsToPlay,
                    Stack<Snapshot> handsToSettle,
                    Stack<Action> actionsTaken) {
        this.bet = bet;
        this.deck.addAll(deck);
        this.dealerHand.addAll(dealerHand);
        this.playerHand.addAll(playerHand);
        this.handsToPlay.addAll(handsToPlay);
        this.handsToSettle.addAll(handsToSettle);
        this.actionsTaken.addAll(actionsTaken);
        this.outcome = determineOutcome(actionsTaken, playerHand, dealerHand);
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

    @Override
    public String toString() {
        return String.format("%s: Bet: %s, Outcome: %s, Deck: %s, ActionsTaken: { %s }, PlayerScore: %s, DealerScore: %s",
                LocalTime.now(), bet, outcome, deck.size(), concat(actionsTaken, ','), score(playerHand), score(dealerHand));
    }
}
