package main.domain;

import java.util.*;

import static main.domain.Outcome.*;
import static main.domain.Rules.*;

public class Game {

    private final Stack<Card> deck;
    private final Stack<Collection <Card>> handsToPlay;
    private final Stack<Collection <Card>> handsToSettle;
    private final Collection<Card> dealerHand;

    private Collection<Card> currentHand;
    private Outcome outcome;
    private int balance;
    private int bet;

    public Game(int balance, int bet, Stack<Card> deck, Collection<Card> dealerHand, Stack<Card> playerHand) {
        this.balance = balance;
        this.bet = bet;
        this.deck = deck;
        this.outcome = UNRESOLVED;
        this.dealerHand = dealerHand;
        this.currentHand = playerHand;
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
    }

    public void stand() throws IllegalStateException {
        if (!handsToPlay.isEmpty()) {
            handsToSettle.add(currentHand);
            currentHand = handsToPlay.pop();
        } else {
            while (score(dealerHand) < 16) {
                if (deck.isEmpty()) {
                    throw new IllegalStateException("No more cards! Quitting...");
                } else {
                    dealerHand.add(deck.pop());
                }
            }
            outcome = determineOutcome();
        }
    }

    public void split() throws NoSuchElementException, EmptyStackException {
        Iterator<Card> iterator = currentHand.iterator();

        currentHand = new Stack<Card>() {{
            add(iterator.next());
            add(deck.pop());
        }};

        Stack<Card> pocketHand = new Stack<Card>() {{
            add(iterator.next());
            add(deck.pop());
        }};

        handsToPlay.add(pocketHand);
    }

    public void hit() throws IllegalStateException {
        if (deck.isEmpty()) {
            throw new IllegalStateException("No more cards! Quitting...");
        } else {
            currentHand.add(deck.pop());
            if (isBust(currentHand)) {
                stand();
            }
        }
    }

    // TODO: Bug - this will double the bet for all unsettled hands;
    public void doubleDown() throws IllegalStateException {
        if (deck.isEmpty()) {
            throw new IllegalStateException("No more cards! Quitting...");
        } else {
            bet *= 2;
            currentHand.add(deck.pop());
            stand();
        }
    }

    public void rewind() {
        if (!handsToSettle.isEmpty()) {
            this.currentHand = handsToSettle.pop();
        }
        outcome = determineOutcome();
    }

    public void settle() {
        switch (determineOutcome()) {
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

    public Snapshot getSnapshot() {
        return new Snapshot(balance, bet, outcome, deck, dealerHand, currentHand, handsToPlay, handsToSettle);
    }

    private Outcome determineOutcome() {
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
}
