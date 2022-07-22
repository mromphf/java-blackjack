package main.domain;

import main.domain.model.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;
import static main.domain.function.CardFunctions.score;
import static main.domain.function.DealerFunctions.freshlyShuffledDeck;
import static main.domain.function.DealerFunctions.openingHand;
import static main.domain.model.Action.*;
import static main.domain.model.ActionLog.emptyActionLog;
import static main.domain.model.Hand.handOf;

public class Round {

    private final static int MINIMUM_DEALER_SCORE = 16;

    private final Account player;
    private final Bets bets;
    private final Deck deck;
    private final Hand dealerHand;
    private final Map<Hand, ActionLog> actionLog = new HashMap<>();
    private final Stack<Hand> handsToPlay = new Stack<>();
    private final Stack<Hand> handsToSettle = new Stack<>();

    private Hand currentHand;

    public static Round newRound(Deck deck, Bets bets) {
        return new Round(deck, bets);
    }

    private Round(Deck deck, Bets bets) {
        this.bets = bets;
        this.deck = deck;

        this.player = bets.accounts()
                .stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        if (deck.size() < 4) {
            refillDeck();
        }

        final Map<String, Hand> openingHands = openingHand(deck);

        this.dealerHand = openingHands.get("dealer");
        this.currentHand = openingHands.get("player");

        actionLog.put(currentHand, emptyActionLog());
    }

    public void record(LocalDateTime timestamp, Action action) {
        actionLog.get(currentHand).put(timestamp, action);
    }

    public void split() throws NoSuchElementException, EmptyStackException {
        if (deck.isEmpty()) {
            refillDeck();
        }

        final Iterator<Card> cardsInHand = currentHand.iterator();

        final Card firstCard = cardsInHand.next();
        final Card secondCard = cardsInHand.next();

        final Hand secondHand = handOf(secondCard);

        currentHand.clear();
        currentHand.add(firstCard);
        currentHand.add(deck.drawCard());

        handsToPlay.add(secondHand);

        actionLog.put(secondHand, emptyActionLog());
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

        handsToSettle.add(currentHand);
        currentHand = handsToPlay.pop();
        currentHand.add(deck.drawCard());
    }

    public void settleNextHand() {
        if (handsToSettle.size() > 0) {
            currentHand = handsToSettle.pop();
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
                bets,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle,
                actionLog
        );
    }
}