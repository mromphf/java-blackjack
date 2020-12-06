package main.domain;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Outcome.*;
import static main.domain.Outcome.UNRESOLVED;
import static main.domain.Rules.*;

public class Game {

    private final Stack<Card> deck;
    private final Stack<Stack <Card>> handsToPlay;
    private final Stack<Stack <Card>> handsToSettle;

    private Collection<Card> dealerHand;
    private Stack<Card> currentHand;
    private int balance;
    private int bet;

    public Game(int balance, Stack<Card> deck) {
        this.balance = balance;
        this.bet = 0;
        this.deck = deck;
        this.dealerHand = new Stack<>();
        this.currentHand = new Stack<>();
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void dealOpeningHand() {
        try {
            Map<String, Stack<Card>> openingHand = openingHand(deck);
            currentHand = openingHand.get("player");
            dealerHand = openingHand.get("dealer");
        } catch (IllegalArgumentException ex) {
            System.out.println("Not enough cards to deal new hand! Quitting...");
            System.exit(0);
        }
    }

    public void split() {
        // TODO: Need safety check for empty deck
        Stack<Card> newHand = new Stack<Card>() {{
            add(currentHand.pop());
        }};
        currentHand.add(deck.pop());
        newHand.add(deck.pop());
        handsToPlay.add(newHand);
    }

    public void doubleBet() {
        bet *= 2;
    }

    public void playNextHand() {
        handsToSettle.add(currentHand);
        currentHand = handsToPlay.pop();
    }

    public void rewind() {
        this.currentHand = handsToSettle.pop();
    }

    public void addCardToDealerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        dealerHand.add(deck.pop());
    }

    public void addCardToPlayerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        currentHand.add(deck.pop());
    }

    public void settle(Outcome outcome) {
        switch (outcome) {
            case WIN:
                balance += bet;
                break;
            case LOSE:
            case BUST:
                balance -= bet;
                break;
            default:
                break;
        }
    }

    public Outcome determineOutcome() {
        if (playerWins(currentHand, dealerHand)) {
            return WIN;
        } else if(isPush(currentHand, dealerHand)) {
            return PUSH;
        } else if (isBust(currentHand)) {
            return BUST;
        } else {
            return LOSE;
        }
    }

    public boolean playerHasBusted() {
        return isBust(currentHand);
    }

    public boolean moreHandsToPlay() {
        return !handsToPlay.isEmpty();
    }

    public boolean dealerShouldHit() {
        return score(dealerHand) < 16;
    }

    public Snapshot getSnapshot() {
        return new Snapshot(balance, bet, UNRESOLVED, deck, dealerHand, currentHand, handsToPlay, handsToSettle);
    }

    public Snapshot getResolvedSnapshot() {
        return new Snapshot(balance, bet, determineOutcome(), deck, dealerHand, currentHand, handsToPlay, handsToSettle);
    }
}
