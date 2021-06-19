package main.domain;


import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.DOUBLE;
import static main.domain.Action.STAND;
import static main.domain.Outcome.UNRESOLVED;
import static main.io.util.StringUtil.actionString;
import static main.domain.Rules.*;
import static main.io.util.StringUtil.playerString;

public class Snapshot {
    private final LocalDateTime timestamp;
    private final UUID accountKey;
    private final int bet;
    private final int maxCards;
    private final Outcome outcome;
    private final Stack<Card> deck = new Stack<>();
    private final Stack<Card> dealerHand = new Stack<>();
    private final Stack<Card> playerHand = new Stack<>();
    private final Stack<Stack<Card>> handsToPlay = new Stack<>();
    private final Stack<Stack<Card>> handsToSettle = new Stack<>();
    private final SortedMap<LocalDateTime, Action> actionsTaken = new TreeMap<>();

    public Snapshot(LocalDateTime timestamp,
                    UUID accountKey,
                    int bet,
                    int maxCards,
                    Stack<Card> deck,
                    Stack<Card> dealerHand,
                    Stack<Card> playerHand,
                    Stack<Stack<Card>> handsToPlay,
                    Stack<Stack<Card>> handsToSettle,
                    SortedMap<LocalDateTime, Action> actionsTaken) {
        this.timestamp = timestamp;
        this.accountKey = accountKey;
        this.bet = bet;
        this.maxCards = maxCards;
        this.deck.addAll(deck);
        this.dealerHand.addAll(dealerHand);
        this.playerHand.addAll(playerHand);
        this.handsToPlay.addAll(handsToPlay);
        this.handsToSettle.addAll(handsToSettle);
        this.actionsTaken.putAll(actionsTaken);
        this.outcome = determineOutcome(
                getActionsTaken(),
                playerHand,
                dealerHand,
                handsToPlay
        );
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getBet() {
        return bet;
    }

    public int getNegativeBet() {
        return Math.negateExact(bet);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public boolean readyToPlayNextHand() {
        return (outcome == UNRESOLVED &&
                !handsToPlay.isEmpty() &&
                (isBust(playerHand) ||
                        actionsTaken.values().stream().anyMatch(
                                a -> a.equals(STAND) || a.equals(DOUBLE))));
    }

    public boolean readyToSettleNextHand() {
        return (!handsToSettle.isEmpty() && isHandResolved());
    }

    public boolean canAffordToSpendMore(int balance) {
        return balance >= bet;
    }

    public boolean isSplitAvailable() {
        return (canSplit(playerHand) &&
                outcome.equals(UNRESOLVED) &&
                !isInsuranceAvailable() &&
                !readyToPlayNextHand());
    }

    public boolean isGameInProgress() {
        return (!canSplit(playerHand) &&
                this.outcome.equals(UNRESOLVED) &&
                !isInsuranceAvailable() &&
                !readyToPlayNextHand());
    }

    public boolean isHandResolved() {
        return !outcome.equals(UNRESOLVED);
    }

    public boolean isRoundResolved() {
        return !outcome.equals(UNRESOLVED) && handsToPlay.isEmpty();
    }

    public boolean allBetsSettled() {
        return !outcome.equals(UNRESOLVED) && handsToPlay.isEmpty() && handsToSettle.isEmpty();
    }

    public boolean isAtLeastOneCardDrawn() {
        return playerHand.size() > 2;
    }

    public boolean isInsuranceAvailable() {
        return insuranceAvailable(dealerHand) && actionsTaken.isEmpty();
    }

    public Collection<Card> getDealerHand() {
        return dealerHand;
    }

    public Stack<Card> getPlayerHand() {
        return playerHand;
    }

    public Stack<Stack<Card>> getHandsToPlay() {
        return handsToPlay;
    }

    public Collection<Action> getActionsTaken() {
        return actionsTaken.values();
    }

    public UUID getAccountKey() {
        return accountKey;
    }

    public float getDeckProgress() {
        return (float) deck.size() / maxCards;
    }

    @Override
    public String toString() {
        return String.format("Outcome: %s, Bet: %s, ActionsTaken: {%s}, Player: %s, Dealer: %s, " +
                        "Deck: %s, Hands to Play: %s, Hands to Settle: %s, Account Key: %s",
                outcome,
                bet,
                actionString(actionsTaken),
                playerString(playerHand),
                playerString(dealerHand),
                deck.size(),
                handsToPlay.size(),
                handsToSettle.size(),
                accountKey);
    }
}
