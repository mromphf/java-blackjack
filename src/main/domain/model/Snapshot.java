package main.domain.model;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.SortedMap;
import java.util.Stack;
import java.util.UUID;

import static java.lang.Math.negateExact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSortedMap;
import static main.domain.function.Rules.determineOutcome;
import static main.domain.util.StringUtil.actionString;
import static main.domain.util.StringUtil.playerString;

public class Snapshot {
    private final LocalDateTime timestamp;
    private final UUID accountKey;
    private final int balance;
    private final int bet;
    private final Outcome outcome;
    private final Collection<Card> deck;
    private final Collection<Card> dealerHand;
    private final Collection<Card> playerHand;
    private final Collection<Hand> handsToPlay;
    private final Collection<Hand> handsToSettle;
    private final SortedMap<LocalDateTime, Action> actionsTaken;

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
        this.deck = unmodifiableCollection(deck);
        this.dealerHand = unmodifiableCollection(dealerHand);
        this.playerHand = unmodifiableCollection(playerHand);
        this.handsToPlay = unmodifiableCollection(handsToPlay);
        this.handsToSettle = unmodifiableCollection(handsToSettle);
        this.actionsTaken = unmodifiableSortedMap(actionsTaken);
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
        return negateExact(bet);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public boolean canAffordToSpendMore() {
        return balance >= bet;
    }

    public Collection<Card> getDealerHand() {
        return dealerHand;
    }

    public Collection<Card> getPlayerHand() {
        return playerHand;
    }

    public Collection<Hand> getHandsToPlay() {
        return handsToPlay;
    }

    public Collection<Hand> getHandsToSettle() {
        return handsToSettle;
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

    public double deckProgress(float maxDeckSize) {
        return deck.size() / maxDeckSize;
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
                getDeckSize(),
                outcome,
                bet,
                handsToPlay.size(),
                handsToSettle.size(),
                actionString(actionsTaken),
                playerString(playerHand),
                playerString(dealerHand));
    }
}
