package main.domain;

import java.util.*;

import static main.domain.Rules.*;

public class Round {

    private final Stack<Card> deck;
    private final Stack<Stack <Card>> handsToPlay;
    private final Stack<Snapshot> handsToSettle;
    private final Stack<Card> dealerHand;
    private final int bet;

    private Stack<Action> actionsTaken;
    private Stack<Card> currentHand;

    public Round(int bet, Stack<Card> deck) {
        this.bet = bet;
        this.deck = deck;
        this.dealerHand = new Stack<>();
        this.currentHand = new Stack<>();
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
        this.actionsTaken = new Stack<>();
    }

    public Round(int bet, Stack<Card> deck, Stack<Card> dealerHand, Stack<Card> playerHand) {
        this.bet = bet;
        this.deck = deck;
        this.dealerHand = dealerHand;
        this.currentHand = playerHand;
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
        this.actionsTaken = new Stack<>();
    }

    public void record(Action action) {
        actionsTaken.add(action);
    }

    public void hit() throws EmptyStackException {
        currentHand.add(deck.pop());
        if (isBust(currentHand) && !handsToPlay.isEmpty()) {
            handsToSettle.add(getSnapshot());
            currentHand = handsToPlay.pop();
            actionsTaken.clear();
        }
    }

    public void doubleDown() throws EmptyStackException{
        currentHand.add(deck.pop());
        stand();
    }

    public void stand() throws EmptyStackException {
        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < 16) {
                dealerHand.add(deck.pop());
            }
        } else {
            handsToSettle.add(getSnapshot());
            currentHand = handsToPlay.pop();
            actionsTaken.clear();
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

    public void rewind() {
        if (!handsToSettle.isEmpty()) {
            Snapshot previousState = handsToSettle.pop();
            currentHand = previousState.getPlayerHand();
            actionsTaken = previousState.getActionsTaken();
        }
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                bet,
                determineOutcome(actionsTaken, currentHand, dealerHand),
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle,
                actionsTaken
        );
    }
}
