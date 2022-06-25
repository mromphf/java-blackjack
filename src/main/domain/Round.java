package main.domain;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static main.domain.Action.*;
import static main.domain.Dealer.freshlyShuffledDeck;
import static main.domain.Dealer.openingHand;
import static main.domain.Hand.handOf;
import static main.domain.Rules.score;

public class Round {

    private final static int MINIMUM_DEALER_SCORE = 16;

    private final Deck deck;
    private final Stack<Hand> handsToPlay = new Stack<>();
    private final Stack<HandToSettle> handsToSettle = new Stack<>();
    private final Hand dealerHand;
    private final int bet;

    private SortedMap<LocalDateTime, Action> actionsTaken = new TreeMap<>();
    private Hand currentHand;

    public Round(Deck deck, int bet) {
        this.bet = bet;
        this.deck = deck;

        if (deck.size() < 4) {
            refillDeck();
        }

        final Map<String, Hand> openingHands = openingHand(deck);

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

        currentHand.add(deck.drawCard());
    }

    public void doubleDown() throws EmptyStackException {
        if (deck.isEmpty()) {
            refillDeck();
        }

        currentHand.add(deck.drawCard());
        stand();
    }

    public void stand() throws EmptyStackException {
        final int deckValue = deck.stream().mapToInt(Card::getBlackjackValue).sum();
        final int deckValueRequired = deckValue - score(dealerHand);

        if (deckValueRequired < MINIMUM_DEALER_SCORE) {
            refillDeck();
        }

        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < MINIMUM_DEALER_SCORE) {
                dealerHand.add(deck.drawCard());
            }
        }
    }

    public void playNextHand() {
        if (deck.isEmpty()) {
            refillDeck();
        }

        handsToSettle.add(new HandToSettle(currentHand, actionsTaken));
        currentHand = handsToPlay.pop();
        currentHand.add(deck.drawCard());
        actionsTaken.values().removeIf(a -> !(a.equals(BUY_INSURANCE) ||
                a.equals(WAIVE_INSURANCE) ||
                a.equals(REFILL)));
    }

    public void split() throws NoSuchElementException, EmptyStackException {
        final Iterator<Card> cardsInHand = currentHand.iterator();

        if (deck.isEmpty()) {
            refillDeck();
        }

        currentHand = handOf(
            cardsInHand.next(),
            deck.drawCard()
        );

        final Hand pocketHand = handOf(cardsInHand.next());

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
        record(now(), REFILL);
        deck.addAll(freshlyShuffledDeck());
    }

    public Snapshot getSnapshot(LocalDateTime timestamp, Account account) {
        return new Snapshot(
                timestamp,
                account.getKey(),
                account.getBalance(),
                bet,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                filterHands(handsToSettle),
                actionsTaken
        );
    }

    private Stack<Hand> filterHands(Stack<HandToSettle> handsToSettle) {
        Stack<Hand> result = new Stack<>();
        for (HandToSettle hand : handsToSettle) {
            result.add(hand.playerHand);
        }
        return result;
    }


    private static class HandToSettle {
        public final Hand  playerHand;
        public final SortedMap<LocalDateTime, Action> actionsTaken;

        public HandToSettle(Hand playerHand, SortedMap<LocalDateTime, Action> actionsTaken) {
            this.playerHand = playerHand;
            this.actionsTaken = actionsTaken;
        }
    }
}