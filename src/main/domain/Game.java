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
    private int balance;
    private int bet;
    private Outcome outcome;

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

    public void stand() {
        if (!handsToPlay.isEmpty()) {
            handsToSettle.add(currentHand);
            currentHand = handsToPlay.pop();
        } else {
            while (score(dealerHand) < 16) {
                addCardToDealerHand();
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

    public void hit() {
        addCardToPlayerHand();
        if (isBust(currentHand)) {
            stand();
        }
    }

    public void doubleDown() {
        // TODO: Bug - this will double the bet for all unsettled hands;
        bet *= 2;
        addCardToPlayerHand();
        stand();
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

        if (balance <= 0) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
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

    private void addCardToDealerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        dealerHand.add(deck.pop());
    }

    private void addCardToPlayerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        currentHand.add(deck.pop());
    }
}
