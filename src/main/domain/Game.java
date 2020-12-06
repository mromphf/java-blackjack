package main.domain;

import java.util.*;

import static main.domain.Outcome.*;
import static main.domain.Rules.*;

public class Game {

    private final Stack<Card> deck;
    private final Stack<Collection <Card>> handsToPlay;
    private final Stack<Snapshot> handsToSettle;
    private final Collection<Card> dealerHand;
    private final int bet;

    private Collection<Card> currentHand;
    private Outcome outcome = UNRESOLVED;
    private boolean doubleDown = false;
    private boolean insuranceSettled = false;
    private int balance;

    public Game(int balance, int bet, Stack<Card> deck) {
        this.balance = balance;
        this.bet = bet;
        this.deck = deck;
        this.dealerHand = new Stack<>();
        this.currentHand = new Stack<>();
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
    }

    public Game(int balance, int bet, Stack<Card> deck, Collection<Card> dealerHand, Stack<Card> playerHand) {
        this.balance = balance;
        this.bet = bet;
        this.deck = deck;
        this.dealerHand = dealerHand;
        this.currentHand = playerHand;
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
    }

    public void stand() throws EmptyStackException {
        insuranceSettled = true;
        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < 16) {
                dealerHand.add(deck.pop());
            }
            outcome = determineOutcome(currentHand, dealerHand);
        } else {
            handsToSettle.add(getSnapshot());
            doubleDown = false;
            currentHand = handsToPlay.pop();
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

    public void hit() throws EmptyStackException {
        insuranceSettled = true;
        currentHand.add(deck.pop());
        if (isBust(currentHand)) {
            if (handsToPlay.isEmpty()) {
                outcome = BUST;
            } else {
                handsToSettle.add(getSnapshot());
                doubleDown = false;
                currentHand = handsToPlay.pop();
            }
        }
    }

    public void doubleDown() throws EmptyStackException{
        insuranceSettled = true;
        currentHand.add(deck.pop());
        doubleDown = true;
        stand();
    }

    public void rewind() {
        if (!handsToSettle.isEmpty()) {
            Snapshot previousState = handsToSettle.pop();
            currentHand = previousState.getPlayerHand();
            doubleDown = previousState.getDoubleDown();
        }
        outcome = determineOutcome(currentHand, dealerHand);
    }

    public void settleInsurance() {
        if (isBlackjack(dealerHand)) {
            outcome = WIN;
        } else {
            balance -= bet;
        }
        insuranceSettled = true;
    }

    public void settleBet() {
        switch (outcome) {
            case WIN:
                this.balance += doubleDown ? bet * 2 : bet;
                break;
            case LOSE:
            case BUST:
                this.balance -= doubleDown ? bet * 2 : bet;
                break;
            default:
                break;
        }
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                balance,
                bet,
                doubleDown,
                insuranceSettled,
                outcome,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle
        );
    }
}
