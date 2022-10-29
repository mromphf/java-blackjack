package com.blackjack.main.domain.predicate;

import com.blackjack.main.domain.model.Action;
import com.blackjack.main.domain.model.Card;
import com.blackjack.main.domain.model.TableView;

import java.util.function.Predicate;

import static com.blackjack.main.domain.function.CardFunctions.*;
import static com.blackjack.main.domain.model.Action.BUY_INSURANCE;
import static com.blackjack.main.domain.model.Action.DOUBLE;
import static com.blackjack.main.domain.model.Outcome.UNRESOLVED;
import static com.blackjack.main.util.LessCode.not;

public class LowOrderPredicate {
    public static final Predicate<TableView> outcomeIsUnresolved = table ->
            table.outcome() != null && table.outcome() == UNRESOLVED;

    public static final Predicate<TableView> outcomeIsResolved = table ->
            table.outcome() != null && table.outcome() != UNRESOLVED;

    public static final Predicate<TableView> atLeastOneCardDrawn = table ->
            table.playerHand().size() > 2;

    public static final Predicate<TableView> handsRemainToBePlayed = table ->
            !table.cardsToPlay().isEmpty();

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

    public static final Predicate<TableView> insurancePurchased = table ->
            table.actionsTaken().stream().anyMatch(action -> action == BUY_INSURANCE);

    public static final Predicate<TableView> playerDoubledDown = table ->
            table.actionsTaken().stream().anyMatch(a -> a.equals(DOUBLE));

    public static final Predicate<TableView> turnEnded = table ->
            table.actionsTaken().stream().anyMatch(Action::turnEnded);

    public static final Predicate<TableView> insurancePaysOut = table ->
            insurancePurchased.test(table) && isBlackjack(table.dealerHand());

    public static final Predicate<TableView> chargeForInsurance = table ->
            insurancePurchased.test(table) && table.actionsTaken().size() == 1;

    public static final Predicate<TableView> startOfRound = table -> (
            outcomeIsUnresolved
                    .and(not(handsRemainToBePlayed))
                    .and(not(handsRemainToBeSettled))
                    .and(noActionsTaken).test(table));

    public static final Predicate<TableView> readyToSettleNextHand = table ->
            outcomeIsResolved.and(handsRemainToBeSettled).test(table);

    public static final Predicate<TableView> isInsuranceAvailable = table ->
            dealerHasAce.and(noActionsTaken).test(table);

    public static final Predicate<TableView> readyToPlayNextHand = table -> (
            handsRemainToBePlayed.and(playerHasBusted.or(turnEnded)).test(table));

    public static final Predicate<TableView> isGameInProgress = table -> (
            !canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<TableView> isSplitAvailable = table -> (
            canSplit(table.playerHand()) &&
                    outcomeIsUnresolved.and(not(isInsuranceAvailable).and(not(readyToPlayNextHand))).test(table));

    public static final Predicate<TableView> allBetsSettled = table -> (
            outcomeIsResolved.and(not(handsRemainToBePlayed)).and(not(handsRemainToBeSettled))).test(table);

    public static final Predicate<TableView> timeForDealerReveal = table -> (
            outcomeIsResolved.and(not(playerHasBusted)).and(
                    not(handsRemainToBeSettled).and(
                            not(handsRemainToBePlayed))).test(table));
}