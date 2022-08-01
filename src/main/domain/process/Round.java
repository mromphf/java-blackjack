package main.domain.process;

import main.domain.model.*;

import java.time.LocalDateTime;
import java.util.*;

import static main.adapter.injection.Bindings.MIN_DEALER_SCORE;
import static main.domain.function.CardFunctions.score;
import static main.domain.function.DealerFunctions.freshlyShuffledDeck;
import static main.domain.function.DealerFunctions.openingHand;
import static main.domain.model.ActionLog.emptyActionLog;
import static main.domain.model.Hand.handOf;

public class Round {

    private final Account player;
    private final Bets bets;
    private final Deck deck;
    private final Hand dealerHand;
    private final Map<Hand, ActionLog> actionLog = new HashMap<>();
    private final Stack<Hand> handsToPlay = new Stack<>();
    private final Stack<Hand> handsToSettle = new Stack<>();
    private final Properties rules;

    private Hand currentHand;

    public static Round newRound(Deck deck, Bets bets, Properties config) {
        return new Round(deck, bets, config);
    }

    private Round(Deck deck, Bets bets, Properties rules) {
        this.bets = bets;
        this.deck = deck;
        this.rules = rules;

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
        hit();
        stand();
    }

    public void stand() throws EmptyStackException {
        final int deckValue = deck.stream().mapToInt(Card::blackjackValue).sum();
        final int deckValueRequired = deckValue - score(dealerHand);
        final int minDealerScore = (int) rules.get(MIN_DEALER_SCORE);

        if (deckValueRequired < minDealerScore) {
            refillDeck();
        }

        if (handsToPlay.isEmpty()) {
            while (score(dealerHand) < minDealerScore) {
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
        currentHand = handsToSettle.pop();
    }

    public void refillDeck() {
        deck.addAll(freshlyShuffledDeck());
    }

    public Table getSnapshot(LocalDateTime timestamp) {
        return new Table(
                timestamp,
                player,
                bets,
                deck,
                dealerHand,
                currentHand,
                handsToPlay,
                handsToSettle,
                actionLog,
                rules
        );
    }
}