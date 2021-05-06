package main.domain;


import java.time.LocalTime;
import java.util.Collection;
import java.util.Stack;

import static main.io.util.StringUtil.concat;
import static main.domain.Rules.*;
import static main.io.util.StringUtil.playerString;

public class Snapshot {
    private final Account account;
    private final int bet;
    private final int maxCards;
    private final Outcome outcome;
    private final Stack<Card> deck = new Stack<>();
    private final Stack<Card> dealerHand = new Stack<>();
    private final Stack<Card> playerHand = new Stack<>();
    private final Stack<Stack<Card>> handsToPlay = new Stack<>();
    private final Stack<Snapshot> handsToSettle = new Stack<>();
    private final Stack<Action> actionsTaken = new Stack<>();

    public Snapshot(Account account,
                    int bet,
                    int maxCards,
                    Stack<Card> deck,
                    Stack<Card> dealerHand,
                    Stack<Card> playerHand,
                    Stack<Stack<Card>> handsToPlay,
                    Stack<Snapshot> handsToSettle,
                    Stack<Action> actionsTaken) {
        this.account = account;
        this.bet = bet;
        this.maxCards = maxCards;
        this.deck.addAll(deck);
        this.dealerHand.addAll(dealerHand);
        this.playerHand.addAll(playerHand);
        this.handsToPlay.addAll(handsToPlay);
        this.handsToSettle.addAll(handsToSettle);
        this.actionsTaken.addAll(actionsTaken);
        this.outcome = determineOutcome(actionsTaken, playerHand, dealerHand);
    }

    public Account getAccount() {
        return account;
    }

    public int getBet() {
        return bet;
    }

    public int getNegativeBet() {
        return Math.negateExact(bet);
    }

    public int getDeckSize() {
        return deck.size();
    }

    public int getMaxCards() {
        return maxCards;
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
        return String.format("%s: %s Bet: %s, ActionsTaken: [ %s ], Player: %s, Dealer: %s, Deck: %s, Hands to Play: %s, Hands to Settle: %s",
                LocalTime.now(), outcome, bet, concat(actionsTaken, ','), playerString(playerHand), playerString(dealerHand), deck.size(), handsToPlay.size(), handsToSettle.size());
    }
}
