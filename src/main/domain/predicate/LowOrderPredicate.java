package main.domain.predicate;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.Snapshot;

import java.util.function.Predicate;

import static main.domain.function.CardFunctions.canSplit;
import static main.domain.function.CardFunctions.isBust;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.util.LessCode.not;

public class LowOrderPredicate {
    public static final Predicate<Snapshot> outcomeIsUnresolved = snapshot ->
            snapshot.getOutcome() != null && snapshot.getOutcome() == UNRESOLVED;

    public static final Predicate<Snapshot> outcomeIsResolved = snapshot ->
            snapshot.getOutcome() != null && snapshot.getOutcome() != UNRESOLVED;

    public static final Predicate<Snapshot> atLeastOneCardDrawn = snapshot ->
            snapshot.getPlayerHand().size() > 2;

    public static final Predicate<Snapshot> handsRemainToBePlayed = snapshot ->
            !snapshot.getHandsToPlay().isEmpty();

    public static final Predicate<Snapshot> noActionsTaken = snapshot ->
            snapshot.getActionsTaken().isEmpty();

    public static final Predicate<Snapshot> handsRemainToBeSettled = snapshot ->
            !snapshot.getHandsToSettle().isEmpty();

    public static final Predicate<Snapshot> playerHasBusted = snapshot ->
            isBust(snapshot.getPlayerHand());

    public static final Predicate<Snapshot> dealerHasAce = snapshot ->
            snapshot.getDealerHand().stream().filter(Card::isFaceUp).limit(1).allMatch(Card::isAce);

    public static final Predicate<Snapshot> playerPurchasedInsurance = snapshot ->
            snapshot.getActionsTaken()
                    .stream()
                    .anyMatch(a -> a == Action.BUY_INSURANCE);

    public static final Predicate<Snapshot> turnEnded = snapshot ->
            snapshot.getActionsTaken().stream().anyMatch(Action::turnEnded);

    public static final Predicate<Snapshot> startOfRound = (snapshot) -> (
            outcomeIsUnresolved.and(noActionsTaken).test(snapshot));

    public static final Predicate<Snapshot> readyToSettleNextHand = snapshot ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(snapshot);

    public static final Predicate<Snapshot> isInsuranceAvailable = snapshot ->
            not(playerPurchasedInsurance).and(dealerHasAce).and(noActionsTaken).test(snapshot);

    public static final Predicate<Snapshot> readyToPlayNextHand = snapshot -> (
            outcomeIsUnresolved.and(handsRemainToBePlayed).and(playerHasBusted.or(turnEnded)).test(snapshot));

    public static final Predicate<Snapshot> isGameInProgress = snapshot -> (
            !canSplit(snapshot.getPlayerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(snapshot));

    public static final Predicate<Snapshot> isSplitAvailable = (snapshot) -> (
            canSplit(snapshot.getPlayerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(snapshot));

    public static final Predicate<Snapshot> allBetsSettled = (snapshot) -> (
            outcomeIsResolved.and(not(handsRemainToBePlayed)).and(not(handsRemainToBeSettled))).test(snapshot);
}