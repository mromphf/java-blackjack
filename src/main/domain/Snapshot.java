package main.domain;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.SortedMap;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Predicate;

import static java.lang.Math.negateExact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSortedMap;
import static main.domain.Action.DOUBLE;
import static main.domain.Action.STAND;
import static main.domain.Outcome.UNRESOLVED;
import static main.domain.Rules.*;
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

    public static final Predicate<Snapshot> outcomeIsUnresolved = snapshot -> snapshot.getOutcome() == UNRESOLVED;
    public static final Predicate<Snapshot> outcomeIsResolved = snapshot -> snapshot.getOutcome() != UNRESOLVED;
    public static final Predicate<Snapshot> atLeastOneCardDrawn = snapshot -> snapshot.getPlayerHand().size() > 2;
    private static final Predicate<Snapshot> handsRemainToBePlayed = snapshot -> !snapshot.getHandsToPlay().isEmpty();
    private static final Predicate<Snapshot> handsRemainToBeSettled = snapshot -> !snapshot.getHandsToSettle().isEmpty();
    private static final Predicate<Snapshot> playerHasBusted = snapshot -> isBust.test(snapshot.getPlayerHand());

    private static final Predicate<Snapshot> stoodOrDoubledDown = snapshot -> snapshot.getActionsTaken()
            .stream()
            .anyMatch(action -> action == STAND || action == DOUBLE);

    public static final Predicate<Snapshot> readyToSettleNextHand = snapshot ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(snapshot);

    public static final Predicate<Snapshot> isInsuranceAvailable = snapshot -> {
            final Collection<Card> dealerHand = snapshot.getDealerHand();
            return (dealerHand.size() > 0 && dealerHand.stream().filter(Card::isAce).count() == 1);
    };

    public static final Predicate<Snapshot> readyToPlayNextHand = snapshot -> (
            outcomeIsUnresolved.and(handsRemainToBePlayed).and((playerHasBusted.or(stoodOrDoubledDown))).test(snapshot));

    public static final Predicate<Snapshot> isGameInProgress = snapshot -> (canSplit.negate().test(snapshot.getPlayerHand()) &&
            outcomeIsUnresolved.and(isInsuranceAvailable.negate().and(readyToPlayNextHand.negate())).test(snapshot));

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

    public boolean isSplitAvailable() {
        return (canSplit.test(playerHand) &&
                outcomeIsUnresolved.and(
                        isInsuranceAvailable.negate().and(
                                readyToPlayNextHand.negate())).test(this));
    }

    public boolean allBetsSettled() {
        return !outcome.equals(UNRESOLVED) && handsToPlay.isEmpty() && handsToSettle.isEmpty();
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
