package main.domain;

import java.util.*;

import static main.domain.Action.*;
import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;
import static main.domain.Rules.*;

public class Round {

    private final Stack<Card> deck;
    private final Stack<Stack<Card>> handsToPlay;
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
        if (deck.isEmpty()) {
            actionsTaken.add(REFILL);
            deck.addAll(shuffle(fresh()));
        }

        currentHand.add(deck.pop());
    }

    public void doubleDown() throws EmptyStackException {
        if (deck.isEmpty()) {
            actionsTaken.add(REFILL);
            deck.addAll(shuffle(fresh()));
        }

        currentHand.add(deck.pop());
        stand();
    }

    public void stand() throws EmptyStackException {
        final int deckValue = deck.stream().mapToInt(Card::getBlackjackValue).sum();

        if ((deckValue - score(dealerHand)) < 16 ) {
            actionsTaken.add(REFILL);
            deck.addAll(shuffle(fresh()));
        }

        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < 16) {
                dealerHand.add(deck.pop());
            }
        }
    }

    public void playNextHand() {
        if (deck.isEmpty()) {
            actionsTaken.add(REFILL);
            deck.addAll(shuffle(fresh()));
        }

        if (!handsToPlay.isEmpty() &&
                (isBust(currentHand) ||
                        actionsTaken.stream().anyMatch(a -> a.equals(STAND) || a.equals(DOUBLE)))) {
            handsToSettle.add(getSnapshot());
            currentHand = handsToPlay.pop();
            currentHand.add(deck.pop());
            actionsTaken.removeIf(a -> !(a.equals(BUY_INSURANCE) || a.equals(NO_INSURANCE) || a.equals(REFILL)));
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
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle,
                actionsTaken
        );
    }
}