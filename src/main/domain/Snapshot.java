package main.domain;


import java.time.LocalDateTime;
import java.util.*;

import static main.domain.util.StringUtil.actionString;
import static main.domain.util.StringUtil.playerString;
import static main.domain.Action.DOUBLE;
import static main.domain.Action.STAND;
import static main.domain.Outcome.UNRESOLVED;
import static main.domain.Rules.*;

public class Snapshot {
    private final LocalDateTime timestamp;
    private final UUID accountKey;
    private final int balance;
    private final int bet;
    private final Outcome outcome;
    private final Stack<Card> deck = new Stack<>();
    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();
    private final Stack<Hand> handsToPlay = new Stack<>();
    private final Stack<Hand> handsToSettle = new Stack<>();
    private final SortedMap<LocalDateTime, Action> actionsTaken = new TreeMap<>();

    public Snapshot(LocalDateTime timestamp,
                    UUID accountKey,
                    int balance,
                    int bet,
                    Stack<Card> deck,
                    Hand dealerHand,
                    Hand playerHand,
                    Stack<Hand> handsToPlay,
                    Stack<Hand> handsToSettle,
                    SortedMap<LocalDateTime, Action> actionsTaken) {
        this.timestamp = timestamp;
        this.accountKey = accountKey;
        this.balance = balance;
        this.bet = bet;
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

    public int getBalance() {
        return balance;
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

    public boolean canAffordToSpendMore() {
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

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Stack<Hand> getHandsToPlay() {
        return handsToPlay;
    }

    public Collection<Action> getActionsTaken() {
        return actionsTaken.values();
    }

    public UUID getAccountKey() {
        return accountKey;
    }

    public int getDeckSize() {
        return deck.size();
    }

    @Override
    public String toString() {
        return String.format("\n\tAccount Key: %s\n\t" +
                        "Balance: $%s,\n\t" +
                        "Cards in Deck: %s,\n\t" +
                        "Outcome: %s,\n\t" +
                        "Bet: $%s,\n\t" +
                        "Hands to Play: %s,\n\t" +
                        "Hands to Settle: %s,\n\t" +
                        "ActionsTaken: {%s\n\t},\n\t" +
                        "Player: %s,\n\t" +
                        "Dealer: %s",
                accountKey,
                balance,
                deck.size(),
                outcome,
                bet,
                handsToPlay.size(),
                handsToSettle.size(),
                actionString(actionsTaken),
                playerString(playerHand),
                playerString(dealerHand));
    }
}
