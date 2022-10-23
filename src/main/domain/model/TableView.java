package main.domain.model;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.concat;
import static java.lang.Math.negateExact;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.function.OutcomeAssessment.settleBet;
import static main.domain.predicate.HighOrderPredicate.determineOutcome;
import static main.util.StringUtil.actionString;
import static main.util.StringUtil.playerString;

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

    public TableView(LocalDateTime timestamp,
                     UUID roundKey,
                     Account player,
                     Bets bets,
                     Deck deck,
                     Hand dealerHand,
                     Hand playerHand,
                     Stack<Hand> handsToPlay,
                     Collection<Hand> handsToSettle,
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
        return dealerHand;
    }

    public Hand playerHand() {
        return playerHand;
    }

    public Collection<Card> allPlayerCards() {
        final Stream<Card> cardsToSettle = handsToSettle
                .stream()
                .flatMap(Collection::stream);
        final Stream<Card> cardsToPlay = handsToPlay
                .stream()
                .flatMap(Collection::stream);
        final Stream<Card> currentCards = playerHand.stream();

        return concat(currentCards, concat(cardsToSettle, cardsToPlay))
                .collect(Collectors.toSet());
    }

    public Collection<Card> cardsToPlay() {
        return handsToPlay.stream()
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public Collection<Hand> handsToSettle() {
        return handsToSettle;
    }

    public Collection<Action> actionsTaken() {
        return actionLog.get(playerHand).values();
    }

    public Action lastActionTaken() {
        return actionLog.get(playerHand).lastEntry().getValue();
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
                        "Account Key: %s\n\t" +
                        "Balance: $%s,\n\t" +
                        "Cards in Deck: %s,\n\t" +
                        "Outcome: %s,\n\t" +
                        "Bet: $%s,\n\t" +
                        "Hands to Play: %s,\n\t" +
                        "Hands to Settle: %s,\n\t" +
                        "ActionsTaken: {%s\n\t},\n\t" +
                        "Player: %s,\n\t" +
                        "Dealer: %s",
                roundKey,
                playerAccountKey(),
                playerBalance(),
                deckSize(),
                outcome,
                bets.get(player),
                handsToPlay.size(),
                handsToSettle.size(),
                actionString(actionLog.get(playerHand)),
                playerString(playerHand),
                playerString(dealerHand));
    }
}
