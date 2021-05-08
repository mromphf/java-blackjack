package main.domain;

import java.util.*;

import static main.domain.Action.*;
import static main.domain.Deck.*;
import static main.domain.Rules.*;

public class Round {

    private final Account account;
    private final Stack<Card> deck;
    private final Stack<Stack<Card>> handsToPlay;
    private final Stack<Snapshot> handsToSettle;
    private final Stack<Card> dealerHand;
    private final int bet;
    private final int maxCards;
    private final int numDecks;

    private Stack<Action> actionsTaken;
    private Stack<Card> currentHand;

    public Round(Account account, int bet, Stack<Card> deck, int maxCards, int numDecks) {
        this.account = account;
        this.bet = bet;
        this.deck = deck;
        this.numDecks = numDecks;
        this.maxCards = maxCards;
        this.dealerHand = new Stack<>();
        this.currentHand = new Stack<>();
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
        this.actionsTaken = new Stack<>();

        if (deck.size() < 4) {
            refillDeck();
        }

        final Map<String, Stack<Card>> openingHands = openingHand(deck);

        dealerHand.addAll(openingHands.get("dealer"));
        currentHand.addAll(openingHands.get("player"));
    }

    public void record(Action action) {
        actionsTaken.add(action);
    }

    public void hit() throws EmptyStackException {
        if (deck.isEmpty()) {
            refillDeck();
        }

        currentHand.add(deck.pop());
    }

    public void doubleDown() throws EmptyStackException {
        if (deck.isEmpty()) {
            refillDeck();
        }

        currentHand.add(deck.pop());
        stand();
    }

    public void stand() throws EmptyStackException {
        final int deckValue = deck.stream().mapToInt(Card::getBlackjackValue).sum();

        if ((deckValue - score(dealerHand)) < 16) {
            refillDeck();
        }

        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < 16) {
                dealerHand.add(deck.pop());
            }
        }
    }

    public void playNextHand() {
        if (deck.isEmpty()) {
            refillDeck();
        }

        handsToSettle.add(getSnapshot());
        currentHand = handsToPlay.pop();
        currentHand.add(deck.pop());
        actionsTaken.removeIf(a -> !(a.equals(BUY_INSURANCE) ||
                a.equals(WAIVE_INSURANCE) ||
                a.equals(REFILL)));
    }

    public void split() throws NoSuchElementException, EmptyStackException {
        final Iterator<Card> iterator = currentHand.iterator();

        if (deck.isEmpty()) {
            refillDeck();
        }

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

    public void refillDeck() {
        actionsTaken.add(REFILL);
        deck.addAll(shuffle(fresh(numDecks)));
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                account,
                bet,
                maxCards,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle,
                actionsTaken
        );
    }
}