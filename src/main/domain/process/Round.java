package main.domain.process;

import main.domain.model.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.UUID.randomUUID;
import static main.adapter.injection.Bindings.MIN_DEALER_SCORE;
import static main.domain.function.CardFunctions.score;
import static main.domain.function.DealerFunctions.freshlyShuffledDeck;
import static main.domain.function.DealerFunctions.openingHand;
import static main.domain.model.ActionLog.emptyActionLog;
import static main.domain.model.Hand.emptyHand;
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
    private final UUID key;

    private Hand currentHand;

    public static Round newRound(Deck deck, Bets bets, Properties config) {
        return new Round(deck, bets, config);
    }

    private Round(Deck deck, Bets bets, Properties rules) {
        this.bets = bets;
        this.deck = deck;
        this.rules = rules;
        this.key = randomUUID();
        this.dealerHand = emptyHand();
        this.currentHand = emptyHand();
        actionLog.put(currentHand, emptyActionLog());

        this.player = bets.accounts()
                .stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void deal() {
        if (deck.size() < (2 * bets.keySet().size() + 2)) {
            refillDeck();
        }

        final Map<String, Hand> openingHands = openingHand(deck);

        dealerHand.addAll(openingHands.get("dealer"));
        currentHand.addAll(openingHands.get("player"));
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
                key,
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