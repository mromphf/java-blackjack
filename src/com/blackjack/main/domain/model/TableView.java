package com.blackjack.main.domain.model;


import com.google.common.collect.Streams;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.blackjack.main.adapter.injection.Bindings.MAX_CARDS;
import static com.blackjack.main.domain.function.Settlement.settleBet;
import static com.blackjack.main.domain.predicate.HighOrderPredicate.determineOutcome;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.timeForDealerReveal;
import static com.blackjack.main.util.StringUtil.dealerString;
import static com.blackjack.main.util.StringUtil.playerString;
import static java.lang.Math.negateExact;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

public class TableView {
    private final Account player;
    private final Bets bets;
    private final Collection<Hand> handsToPlay;
    private final Collection<Hand> handsToSettle;
    private final Deck deck;
    private final Hand dealerHand;
    private final Hand playerHand;
    private final LocalDateTime timestamp;
    private final Map<Hand, ActionLog> actionLog;
    private final Outcome outcome;
    private final Properties config;
    private final UUID roundKey;

    public TableView(LocalDateTime timestamp, UUID roundKey,
                     Account player, Bets bets, Deck deck,
                     Hand dealerHand, Hand playerHand,
                     Stack<Hand> handsToPlay, Collection<Hand> handsToSettle,
                     Map<Hand, ActionLog> actionsTaken,
                     Properties config) {
        this.timestamp = timestamp;
        this.roundKey = roundKey;
        this.player = player;
        this.bets = bets;
        this.deck = deck;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
        this.handsToPlay = handsToPlay;
        this.handsToSettle = handsToSettle;
        this.actionLog = unmodifiableMap(actionsTaken);
        this.outcome = determineOutcome(this);
        this.config = config;
    }

    public UUID roundKey() { return roundKey; }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String balanceText() {
        return format("Balance $%s", playerBalance());
    }

    public int bet() {
        return bets.get(player);
    }

    public int negativeBet() {
        return negateExact(bets.get(player));
    }

    public int playerBalance() {
        return player.getBalance() + settleBet(this);
    }

    public Outcome outcome() {
        return outcome;
    }

    public boolean canAffordToSpendMore() {
        return playerBalance() >= bets.get(player);
    }

    public Collection<Card> dealerHand() {
        if (timeForDealerReveal.test(this)) {
            return dealerHand.stream()
                    .map(Card::faceUp)
                    .collect(toSet());
        }

        return dealerHand;
    }

    public Hand playerHand() {
        return playerHand;
    }

    public Collection<Card> allCardsInPlay() {
        return concat(playerHand.stream(), dealerHand().stream()).collect(toSet());
    }

    public Collection<Card> allPlayerCards() {
        final Stream<Card> cardsToSettle = handsToSettle
                .stream()
                .flatMap(Collection::stream);
        final Stream<Card> cardsToPlay = handsToPlay
                .stream()
                .flatMap(Collection::stream);
        final Stream<Card> currentCards = playerHand.stream();

        return Streams.concat(currentCards, concat(cardsToSettle, cardsToPlay))
                .collect(toSet());
    }

    public Collection<Card> cardsToPlay() {
        return handsToPlay.stream()
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public Collection<Hand> handsToSettle() {
        return handsToSettle;
    }

    public Collection<Hand> handsToPlay() {
        return handsToPlay;
    }

    public Collection<Action> actionsTaken() {
        return actionLog.get(playerHand).values();
    }

    public Map<LocalDateTime, Action> actionsTimestamped() {
        return actionLog.get(playerHand);
    }

    public Optional<Action> lastActionTaken() {
        if (actionLog.get(playerHand).isEmpty()) {
            return empty();
        } else {
            return Optional.of(actionLog.get(playerHand)
                    .lastEntry()
                    .getValue());
        }
    }

    public UUID playerAccountKey() {
        return player.key();
    }

    public Deck deck() {
        return deck;
    }

    public UUID deckKey() {
        return deck.key();
    }

    public int deckSize() {
        return deck.size();
    }

    public double deckProgress() {
        return deck.size() / (double) config.get(MAX_CARDS);
    }

    @Override
    public String toString() {
        return format("\n\tRound Key: %s\n\t" +
                        "Cards in Deck: %s,\n\t" +
                        "Outcome: %s,\n\t" +
                        "Player: %s,\n\t" +
                        "Dealer: %s",
                roundKey, deckSize(), outcome,
                playerString(this), dealerString(dealerHand));
    }
}
