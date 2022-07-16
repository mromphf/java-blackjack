package main.domain;

import main.domain.model.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static main.domain.function.Dealer.freshlyShuffledDeck;
import static main.domain.function.Dealer.openingHand;
import static main.domain.function.CardFunctions.score;
import static main.domain.model.Action.*;
import static main.domain.model.Hand.handOf;

public class Round {

    private final static int MINIMUM_DEALER_SCORE = 16;

    private final Account player;
    private final Deck deck;
    private final Stack<Card> cardsToPlay = new Stack<>();
    private final Stack<HandToSettle> handsToSettle = new Stack<>();
    private final Hand dealerHand;
    private final int bet;

    private SortedMap<LocalDateTime, Action> actionsTaken = new TreeMap<>();
    private Hand currentHand;

    public static Round newRound(Account player, Deck deck, int bet) {
        return new Round(player.debit(bet), deck, bet);
    }

    private Round(Account player, Deck deck, int bet) {
        this.bet = bet;
        this.deck = deck;
        this.player = player;

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

        if (cardsToPlay.isEmpty()) {
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
        currentHand = handOf(cardsToPlay.pop());
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

        cardsToPlay.add(cardsInHand.next());
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

    public TableView getSnapshot(LocalDateTime timestamp) {
        return new TableView(
                timestamp,
                player,
                bet,
                deck,
                dealerHand,
                currentHand,
                cardsToPlay,
                fetchHandsToSettle(handsToSettle),
                actionsTaken
        );
    }

    private Collection<Hand> fetchHandsToSettle(Stack<HandToSettle> handsToSettle) {
        return handsToSettle.stream()
                .map(HandToSettle::playerHand)
                .collect(toList());
    }

    private static class HandToSettle {
        public final Hand playerHand;
        public final SortedMap<LocalDateTime, Action> actionsTaken;

        public HandToSettle(Hand playerHand, SortedMap<LocalDateTime, Action> actionsTaken) {
            this.playerHand = playerHand;
            this.actionsTaken = actionsTaken;
        }

        public Hand playerHand() {
            return playerHand;
        }
    }
}