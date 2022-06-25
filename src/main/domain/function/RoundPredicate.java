package main.domain.function;

import main.domain.model.Card;
import main.domain.model.Snapshot;

import java.util.Collection;
import java.util.function.Predicate;

import static main.domain.model.Action.DOUBLE;
import static main.domain.model.Action.STAND;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.domain.function.Rules.canSplit;
import static main.domain.function.Rules.isBust;
import static main.domain.util.LessCode.not;

public class RoundPredicate {

    public static final Predicate<Snapshot> isSplitAvailable = p_isSplitAvailable();

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

    private static Predicate<Snapshot> p_isSplitAvailable() {
        return (snapshot) -> (canSplit.test(snapshot.getPlayerHand()) &&
                outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(snapshot));
    }
}
