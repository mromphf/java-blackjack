package main.domain.predicate;

import main.domain.model.Outcome;
import main.domain.model.Table;

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

    public static final Predicate<Table> unresolvedOutcome = table -> (
            (outcomeIsUnresolved.and(playerHasBusted).and(handsRemainToBePlayed)).or(
                    not(playerHasBusted).and(handsRemainToBePlayed).and(turnEnded)).or(
                    not(playerHasBusted).and(noActionsTaken.or(not(turnEnded)))).test(table));

    private static final Predicate<Table> pushOutcome = table -> (
            turnEnded.and(not(readyToPlayNextHand)).test(table) &&
                    isPush(table.playerHand(), table.dealerHand()));

    private static final Predicate<Table> blackjackOutcome = table -> (
            turnEnded.and(not(readyToPlayNextHand)).test(table) &&
                    playerWins(table.playerHand(), table.dealerHand()) &&
                    isBlackjack(table.playerHand()));

    private static final Predicate<Table> winOutcome = (table) -> (
            turnEnded.test(table) &&
                    playerWins(table.playerHand(), table.dealerHand()) &&
                    !(isBlackjack(table.playerHand())));

    private static final Predicate<Table> bustOutcome = (table) -> (
            atLeastOneActionTaken.and(playerHasBusted).test(table));

    private static final Predicate<Table> loseOutcome = (table) -> (
            turnEnded.and(not(playerHasBusted)).and(not(readyToPlayNextHand)).test(table) &&
                    !isPush(table.playerHand(), table.dealerHand()) &&
                    !playerWins(table.playerHand(), table.dealerHand()));

    public static Outcome determineOutcome(Table table) {
        return predicateOutcomeMap.get(predicateOutcomeMap.keySet()
                .stream()
                .filter(predicate -> predicate.test(table))
                .findFirst()
                .orElse(unresolvedOutcome));
    }

    private static final Map<Predicate<Table>, Outcome> predicateOutcomeMap = new HashMap<Predicate<Table>, Outcome>() {{
        put(blackjackOutcome, BLACKJACK);
        put(pushOutcome, PUSH);
        put(winOutcome, WIN);
        put(bustOutcome, BUST);
        put(loseOutcome, LOSE);
        put(unresolvedOutcome, UNRESOLVED);
    }};
}
