package main.domain.function;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.Outcome;
import main.domain.model.Snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static main.domain.function.Rules.*;
import static main.domain.model.Outcome.*;
import static main.domain.util.LessCode.not;

public class RoundPredicate {
    public static final Predicate<Snapshot> outcomeIsUnresolved = snapshot ->
            snapshot.getOutcome() != null && snapshot.getOutcome() == UNRESOLVED;

    public static final Predicate<Snapshot> outcomeIsResolved = snapshot ->
            snapshot.getOutcome() != null && snapshot.getOutcome() != UNRESOLVED;

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

    public static final Predicate<Snapshot> unresolvedOutcome = (snapshot) -> (
            (outcomeIsUnresolved.and(playerHasBusted).and(handsRemainToBePlayed)).or(
                    not(playerHasBusted).and(handsRemainToBePlayed).and(turnEnded)).or(
                    not(playerHasBusted).and(noActionsTaken.or(not(turnEnded)))).test(snapshot));

    private static final Predicate<Snapshot> pushOutcome = (snapshot) -> (
            outcomeIsUnresolved.and(turnEnded).test(snapshot) &&
                    isPush(snapshot.getPlayerHand(), snapshot.getDealerHand()) &&
                    !playerWins(snapshot.getPlayerHand(), snapshot.getDealerHand()));

    private static final Predicate<Snapshot> blackjackOutcome = (snapshot) -> (
            turnEnded.and(not(noActionsTaken)).test(snapshot) &&
                    playerWins(snapshot.getPlayerHand(), snapshot.getDealerHand()) &&
                    isBlackjack.test(snapshot.getPlayerHand()));

    private static final Predicate<Snapshot> winOutcome = (snapshot) -> (
            turnEnded.and(not(noActionsTaken)).test(snapshot) &&
                    playerWins(snapshot.getPlayerHand(), snapshot.getDealerHand()) &&
                    not(isBlackjack).test(snapshot.getPlayerHand()));

    private static final Predicate<Snapshot> bustOutcome = (snapshot) -> (
            not(noActionsTaken).and(playerHasBusted).test(snapshot));

    private static final Predicate<Snapshot> loseOutcome = (snapshot) -> (
            turnEnded.and(not(noActionsTaken)).and(not(playerHasBusted)).test(snapshot) &&
                    !playerWins(snapshot.getPlayerHand(), snapshot.getDealerHand()));

    public static Outcome determineOutcome(Snapshot snapshot) {
        return predicateOutcomeMap.get(predicateOutcomeMap.keySet()
                .stream()
                .filter(predicate -> predicate.test(snapshot))
                .findFirst()
                .orElse(unresolvedOutcome));

    }

    private static final Map<Predicate<Snapshot>, Outcome> predicateOutcomeMap = new HashMap<Predicate<Snapshot>, Outcome>() {{
        put(blackjackOutcome, BLACKJACK);
        put(pushOutcome, PUSH);
        put(winOutcome, WIN);
        put(bustOutcome, BUST);
        put(loseOutcome, LOSE);
        put(unresolvedOutcome, UNRESOLVED);
    }};
}