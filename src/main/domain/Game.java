package main.domain;

import main.usecase.Outcome;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Game {

    private final Stack<Stack <Card>> handsToPlay;
    private final Stack<Stack <Card>> handsToSettle;
    private final Stack<Card> deck;
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
            handsToPlay.removeAllElements();
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
            return Outcome.WIN;
        } else if(isPush(currentHand, dealerHand)) {
            return Outcome.PUSH;
        } else if (isBust(currentHand)) {
            return Outcome.BUST;
        } else {
            return Outcome.LOSE;
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
        final boolean atLeastOneCardDrawn = currentHand.size() > 2;
        return new Snapshot(balance, bet, deck.size(), atLeastOneCardDrawn, handsToSettle.isEmpty(), dealerHand, currentHand, handsToPlay);
    }
}
