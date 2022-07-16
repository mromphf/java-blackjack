package main.domain.predicate;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.TableView;

import java.util.function.Predicate;

import static main.domain.function.CardFunctions.canSplit;
import static main.domain.function.CardFunctions.isBust;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.util.LessCode.not;

public class LowOrderPredicate {
    public static final Predicate<TableView> outcomeIsUnresolved = table ->
            table.outcome() != null && table.outcome() == UNRESOLVED;

    public static final Predicate<TableView> outcomeIsResolved = table ->
            table.outcome() != null && table.outcome() != UNRESOLVED;

    public static final Predicate<TableView> atLeastOneCardDrawn = table ->
            table.playerHand().size() > 2;

    public static final Predicate<TableView> handsRemainToBePlayed = table ->
            !table.handsToPlay().isEmpty();

    public static final Predicate<TableView> noActionsTaken = table ->
            table.actionsTaken().isEmpty();

    public static final Predicate<TableView> atLeastOneActionTaken = table ->
            table.actionsTaken().size() > 0;

    public static final Predicate<TableView> handsRemainToBeSettled = table ->
            !table.handsToSettle().isEmpty();

    public static final Predicate<TableView> playerHasBusted = table ->
            isBust(table.playerHand());

    public static final Predicate<TableView> dealerHasAce = table ->
            table.dealerHand().stream().filter(Card::isFaceUp).limit(1).allMatch(Card::isAce);

    public static final Predicate<TableView> playerPurchasedInsurance = table ->
            table.actionsTaken()
                    .stream()
                    .anyMatch(action -> action == BUY_INSURANCE);

    public static final Predicate<TableView> turnEnded = table ->
            table.actionsTaken().stream().anyMatch(Action::turnEnded);

    public static final Predicate<TableView> startOfRound = table -> (
            outcomeIsUnresolved.and(noActionsTaken).test(table));

    public static final Predicate<TableView> readyToSettleNextHand = table ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(table);

    public static final Predicate<TableView> isInsuranceAvailable = table ->
            not(playerPurchasedInsurance).and(dealerHasAce).and(noActionsTaken).test(table);

    public static final Predicate<TableView> readyToPlayNextHand = table -> (
            outcomeIsUnresolved.and(handsRemainToBePlayed).and(playerHasBusted.or(turnEnded)).test(table));

    public static final Predicate<TableView> isGameInProgress = table -> (
            !canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<TableView> isSplitAvailable = table -> (
            canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<TableView> allBetsSettled = table -> (
            outcomeIsResolved.and(not(handsRemainToBePlayed)).and(not(handsRemainToBeSettled))).test(table);
}