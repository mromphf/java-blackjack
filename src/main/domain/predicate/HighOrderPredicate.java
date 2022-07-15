package main.domain.predicate;

import main.domain.model.Outcome;
import main.domain.model.Snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static main.domain.function.CardFunctions.*;
import static main.domain.function.CardFunctions.playerWins;
import static main.domain.model.Outcome.*;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.domain.predicate.LowOrderPredicate.*;
import static main.util.LessCode.not;

public class HighOrderPredicate {

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
                    isBlackjack(snapshot.getPlayerHand()));

    private static final Predicate<Snapshot> winOutcome = (snapshot) -> (
            turnEnded.and(not(noActionsTaken)).test(snapshot) &&
                    playerWins(snapshot.getPlayerHand(), snapshot.getDealerHand()) &&
                    !(isBlackjack(snapshot.getPlayerHand())));

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
