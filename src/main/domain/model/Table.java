package main.domain.model;


import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.negateExact;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;
import static main.domain.function.CardFunctions.concealedScore;
import static main.domain.function.CardFunctions.score;
import static main.domain.function.OutcomeAssessment.settleBet;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.domain.predicate.HighOrderPredicate.determineOutcome;
import static main.util.StringUtil.actionString;
import static main.util.StringUtil.playerString;

public class Table {
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

    public Table(LocalDateTime timestamp,
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

    public Collection<Card> playerHand() {
        return playerHand;
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

    public UUID playerAccountKey() {
        return player.key();
    }

    public int deckSize() {
        return deck.size();
    }

    public double deckProgress() {
        return deck.size() / (double) config.get("maxCards");
    }

    public int dealerScore() {
        if (outcome == UNRESOLVED) {
            return concealedScore(dealerHand);
        } else {
            return score(dealerHand);
        }
    }

    public int playerScore() {
        return score(playerHand);
    }

    @Override
    public String toString() {
        return format("\n\tAccount Key: %s\n\t" +
                        "Balance: $%s,\n\t" +
                        "Cards in Deck: %s,\n\t" +
                        "Outcome: %s,\n\t" +
                        "Bet: $%s,\n\t" +
                        "Hands to Play: %s,\n\t" +
                        "Hands to Settle: %s,\n\t" +
                        "ActionsTaken: {%s\n\t},\n\t" +
                        "Player: %s,\n\t" +
                        "Dealer: %s",
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
