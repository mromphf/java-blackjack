package main.domain;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Deck.openingHand;
import static main.domain.Rules.score;

public class Round {

    private final Stack<Card> deck;
    private final Stack<Stack<Card>> handsToPlay;
    private final Stack<HandToSettle> handsToSettle;
    private final Stack<Card> dealerHand;
    private final int bet;
    private final int maxCards;

    private SortedMap<LocalDateTime, Action> actionsTaken;
    private Stack<Card> currentHand;

    public Round(Stack<Card> deck, int bet, int maxCards) {
        this.bet = bet;
        this.deck = deck;
        this.maxCards = maxCards;
        this.handsToPlay = new Stack<>();
        this.handsToSettle = new Stack<>();
        this.actionsTaken = new TreeMap<>();

        if (deck.size() < 4) {
            refillDeck();
        }

        final Map<String, Stack<Card>> openingHands = openingHand(deck);

        this.dealerHand = openingHands.get("dealer");
        this.currentHand = openingHands.get("player");
    }

    public void record(LocalDateTime timestamp, Action action) {
        actionsTaken.put(timestamp, action);
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

        handsToSettle.add(new HandToSettle(currentHand, actionsTaken));
        currentHand = handsToPlay.pop();
        currentHand.add(deck.pop());
        actionsTaken.values().removeIf(a -> !(a.equals(BUY_INSURANCE) ||
                a.equals(WAIVE_INSURANCE) ||
                a.equals(REFILL)));
    }

    public void split() throws NoSuchElementException, EmptyStackException {
        final Iterator<Card> cardsInHand = currentHand.iterator();

        if (deck.isEmpty()) {
            refillDeck();
        }

        currentHand = new Stack<Card>() {{
            add(cardsInHand.next());
            add(deck.pop());
        }};

        Stack<Card> pocketHand = new Stack<Card>() {{
            add(cardsInHand.next());
        }};

        handsToPlay.add(pocketHand);
    }

    public void rewind() {
        if (!handsToSettle.isEmpty()) {
            HandToSettle previousState = handsToSettle.pop();
            currentHand = previousState.playerHand;
            actionsTaken = previousState.actionsTaken;
        }
    }

    public void refillDeck() {
        record(LocalDateTime.now(), REFILL);
        deck.addAll(freshlyShuffledDeck());
    }

    public Snapshot getSnapshot(LocalDateTime timestamp, Account account) {
        return new Snapshot(
                timestamp,
                account.getKey(),
                account.getBalance(),
                bet,
                maxCards,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                filterHands(handsToSettle),
                actionsTaken
        );
    }

    private Stack<Stack<Card>> filterHands(Stack<HandToSettle> handsToSettle) {
        Stack<Stack<Card>> result = new Stack<>();
        for (HandToSettle hand : handsToSettle) {
            result.add(hand.playerHand);
        }
        return result;
    }


    private static class HandToSettle {
        public final Stack<Card> playerHand;
        public final SortedMap<LocalDateTime, Action> actionsTaken;

        public HandToSettle(Stack<Card> playerHand, SortedMap<LocalDateTime, Action> actionsTaken) {
            this.playerHand = playerHand;
            this.actionsTaken = actionsTaken;
        }
    }
}