package main.domain.predicate;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.Table;

import java.util.function.Predicate;

import static main.domain.function.CardFunctions.canSplit;
import static main.domain.function.CardFunctions.isBust;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.util.LessCode.not;

public class LowOrderPredicate {
    public static final Predicate<Table> outcomeIsUnresolved = table ->
            table.outcome() != null && table.outcome() == UNRESOLVED;

    public static final Predicate<Table> outcomeIsResolved = table ->
            table.outcome() != null && table.outcome() != UNRESOLVED;

    public static final Predicate<Table> atLeastOneCardDrawn = table ->
            table.playerHand().size() > 2;

    public static final Predicate<Table> handsRemainToBePlayed = table ->
            !table.cardsToPlay().isEmpty();

    public static final Predicate<Table> noActionsTaken = table ->
            table.actionsTaken().isEmpty();

    public static final Predicate<Table> atLeastOneActionTaken = table ->
            table.actionsTaken().size() > 0;

    public static final Predicate<Table> handsRemainToBeSettled = table ->
            !table.handsToSettle().isEmpty();

    public static final Predicate<Table> playerHasBusted = table ->
            isBust(table.playerHand());

    public static final Predicate<Table> dealerHasAce = table ->
            table.dealerHand().stream().filter(Card::isFaceUp).limit(1).allMatch(Card::isAce);

    public static final Predicate<Table> insurancePurchased = table ->
            table.actionsTaken().stream().anyMatch(action -> action == BUY_INSURANCE);

    public static final Predicate<Table> turnEnded = table ->
            table.actionsTaken().stream().anyMatch(Action::turnEnded);

    public static final Predicate<Table> chargeForInsurance = table ->
            insurancePurchased.test(table) && table.actionsTaken().size() == 1;

    public static final Predicate<Table> startOfRound = table -> (
            outcomeIsUnresolved
                    .and(not(handsRemainToBePlayed))
                    .and(not(handsRemainToBeSettled))
                    .and(noActionsTaken).test(table));

    public static final Predicate<Table> readyToSettleNextHand = table ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(table);

    public static final Predicate<Table> isInsuranceAvailable = table ->
            dealerHasAce.and(noActionsTaken).test(table);

    public static final Predicate<Table> readyToPlayNextHand = table -> (
            handsRemainToBePlayed.and(playerHasBusted.or(turnEnded)).test(table));

    public static final Predicate<Table> isGameInProgress = table -> (
            !canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<Table> isSplitAvailable = table -> (
            canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<Table> allBetsSettled = table -> (
            outcomeIsResolved.and(not(handsRemainToBePlayed)).and(not(handsRemainToBeSettled))).test(table);

    public static final Predicate<Table> timeForDealerReveal = table -> (
            outcomeIsResolved.and(not(playerHasBusted)).and(
                    not(handsRemainToBeSettled).and(
                            not(handsRemainToBePlayed))).test(table));
}