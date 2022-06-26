package main.domain.function;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.Snapshot;

import java.util.function.Predicate;

import static main.domain.function.Rules.isBust;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.domain.function.Rules.canSplit;
import static main.domain.util.LessCode.not;

public class RoundPredicate {

    public static final Predicate<Snapshot> outcomeIsUnresolved = snapshot -> snapshot.getOutcome() == UNRESOLVED;

    public static final Predicate<Snapshot> outcomeIsResolved = snapshot -> snapshot.getOutcome() != UNRESOLVED;

    public static final Predicate<Snapshot> atLeastOneCardDrawn = snapshot -> snapshot.getPlayerHand().size() > 2;

    private static final Predicate<Snapshot> handsRemainToBePlayed = snapshot -> !snapshot.getHandsToPlay().isEmpty();

    public static final Predicate<Snapshot> noActionsTaken = snapshot -> snapshot.getActionsTaken().isEmpty();

    private static final Predicate<Snapshot> handsRemainToBeSettled = snapshot -> !snapshot.getHandsToSettle().isEmpty();

    private static final Predicate<Snapshot> playerHasBusted = snapshot -> isBust(snapshot.getPlayerHand());

        public static final Predicate<Snapshot> turnEnded = snapshot ->
            snapshot.getActionsTaken().stream().anyMatch(Action::turnEnded);

    public static final Predicate<Snapshot> readyToSettleNextHand = snapshot ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(snapshot);

    public static final Predicate<Snapshot> isInsuranceAvailable = snapshot ->
            snapshot.getDealerHand().stream().filter(Card::isFaceUp).limit(1).allMatch(Card::isAce) &&
                    snapshot.getActionsTaken().size() == 0;

    public static final Predicate<Snapshot> readyToPlayNextHand = snapshot -> (
            outcomeIsUnresolved.and(handsRemainToBePlayed).and((playerHasBusted.or(turnEnded))).test(snapshot));

    public static final Predicate<Snapshot> isGameInProgress = snapshot -> (
            not(canSplit).test(snapshot.getPlayerHand()) &&
            outcomeIsUnresolved.and(isInsuranceAvailable.negate().and(readyToPlayNextHand.negate())).test(snapshot));

    public static final Predicate<Snapshot> isSplitAvailable = (snapshot) -> (
            canSplit.test(snapshot.getPlayerHand()) &&
            outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(snapshot));

    public static final Predicate<Snapshot> allBetsSettled = (snapshot) -> (
            outcomeIsResolved.and(not(handsRemainToBePlayed)).and(not(handsRemainToBeSettled))).test(snapshot);

    public static final Predicate<Snapshot> roundNotResolved = (snapshot) -> (
            (playerHasBusted.and(handsRemainToBePlayed)).or(
                not(playerHasBusted).and(handsRemainToBePlayed).and(turnEnded)).or(
                       not(playerHasBusted).and(noActionsTaken.or(not(turnEnded)))).test(snapshot));
}
