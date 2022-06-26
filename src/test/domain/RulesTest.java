package test.domain;

import main.domain.model.Card;
import main.domain.model.Hand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static main.domain.function.Rules.*;
import static main.domain.model.Card.card;
import static main.domain.model.Hand.handOf;
import static main.domain.model.Rank.*;
import static main.domain.model.Suit.*;
import static org.junit.jupiter.api.Assertions.*;
import static test.domain.anonymous.Anonymous.anonCard;
import static test.domain.anonymous.Anonymous.anonSuit;

public class RulesTest {

    @ParameterizedTest
    @MethodSource("blackjackHands")
    public void isBlackjack_shouldReturnTrue_whenGivenABlackjackHand(Hand hand) {
        assertTrue(isBlackjack.test(hand));
    }

    @ParameterizedTest
    @MethodSource("nonBlackjackHands")
    public void isBlackjack_shouldReturnFalse_whenGivenANonBlackjackHand(Hand hand) {
        assertFalse(isBlackjack.test(hand));
    }

    @ParameterizedTest
    @MethodSource("bustedHands")
    public void isBusted_shouldReturnTrue_whenGivenABustedHand(Hand hand) {
        assertTrue(isBust(hand));
    }

    @ParameterizedTest
    @MethodSource("nonBustedHands")
    public void isBusted_shouldReturnFalse_whenGivenANonBustedHand(Hand hand) {
        assertFalse(isBust(hand));
    }

    @Test
    public void isPush_shouldReturnTrue_whenTotalValueOfTwoHandsIsEqual() {
        assertTrue(isPush(
                handOf(card(TEN, anonSuit()), card(JACK, anonSuit())),
                handOf(card(FIVE, HEARTS), card(FIVE, DIAMONDS), card(KING, CLUBS))
        ));
    }

    @Test
    public void isPush_shouldReturnFalse_whenTotalValueOfTwoHandsIsUnequal() {
        assertFalse(isPush(
                handOf(card(TEN, HEARTS), card(FIVE, CLUBS)),
                handOf(card(SEVEN, HEARTS), card(KING, CLUBS))
        ));
    }

    @ParameterizedTest
    @MethodSource("scoreEvaluations")
    public void score_shouldEvaluateCorrectly_whenGivenACollectionOfHandsAndScores(Map<Integer, Collection<Hand>> scoresToHands) {
        for (Map.Entry<Integer, Collection<Hand>> scoresToHand : scoresToHands.entrySet()) {
            scoresToHand.getValue().forEach(hand -> assertEquals(scoresToHand.getKey(), score(hand)));
        }
    }

    @Test
    public void concealedScore_shouldReturnFive_whenGivenAFiveFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(EIGHT, SPADES));
        }};

        assertEquals(5, concealedScore(cards));
    }

    @Test
    public void concealedScore_shouldReturnEleven_whenGivenAnAceFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(card(ACE, HEARTS));
            add(card(EIGHT, SPADES));
        }};

        assertEquals(11, concealedScore(cards));
    }

    @ParameterizedTest
    @MethodSource("aceHands")
    public void atLeastOneAce_shouldReturnTrue_whenGivenAtLeastOneAce(Hand hand) {
        assertTrue(atLeastOneAce(hand));
    }

    @Test
    public void atLeastOneAce_shouldReturnTrue_whenGivenAtLeastOneAce() {
        assertTrue(atLeastOneAce(handOf(
                card(ACE, HEARTS),
                card(EIGHT, SPADES)
        )));
    }

    @Test
    public void atLeastOneAce_shouldReturnFalse_whenGivenNoAces() {
        assertFalse(atLeastOneAce(handOf(
                        card(FIVE, CLUBS),
                        card(EIGHT, DIAMONDS)
                )
        ));
    }

    @Test
    public void hardTotal_shouldReturnThirteen_whenGivenAnAceAndATwo() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(TWO, SPADES));
        }};

        assertEquals(13, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyOne_whenGivenAnAceAndATen() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(TEN, SPADES));
        }};

        assertEquals(21, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyFive_whenGivenAnAceAndATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(TEN, SPADES));
            add(card(FOUR, CLUBS));
        }};

        assertEquals(25, hardTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFive_whenGivenAnAceAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(FOUR, CLUBS));
        }};

        assertEquals(5, softTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFifteen_whenGivenAnAceATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(TEN, SPADES));
            add(card(FOUR, CLUBS));
        }};

        assertEquals(15, softTotal(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsLessThanOrEqualToTwentyOne() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(ACE, HEARTS));
            add(card(FIVE, SPADES));
            add(card(FIVE, SPADES));
        }};

        assertTrue(hardTotalIsFavourable(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsGreaterThanTwentyOne() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(ACE, HEARTS));
            add(card(SIX, SPADES));
            add(card(FIVE, SPADES));
        }};

        assertFalse(hardTotalIsFavourable(cards));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenPlayerScoreIsTwentyAndDealerScoreIsNineteen() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(card(ACE, HEARTS));
            add(card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(card(FOUR, HEARTS));
            add(card(SIX, CLUBS));
            add(card(NINE, SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenDealerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(QUEEN, CLUBS));
            add(card(KING, SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenPlayerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(KING, SPADES));
            add(card(FIVE, DIAMONDS));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(NINE, SPADES));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenPlayerBothPlayerAndDealerBust() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(KING, SPADES));
            add(card(EIGHT, DIAMONDS));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(TEN, SPADES));
            add(card(TWO, SPADES));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenHandsAreOfEqualValue() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(FIVE, CLUBS));
            add(card(ACE, HEARTS));
            add(card(NINE, DIAMONDS));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenTwoEqualCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(FIVE, SPADES));
        }};

        assertTrue(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenATenAndAQueen() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(TEN, HEARTS));
            add(card(QUEEN, SPADES));
        }};

        assertTrue(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenTwoUnequalCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(SIX, SPADES));
        }};

        assertFalse(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenMoreThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(FIVE, SPADES));
            add(card(FIVE, CLUBS));
        }};

        assertFalse(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenFewerThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(card(FIVE, HEARTS));
        }};

        assertFalse(canSplit.test(cards));
    }

    private static Stream<Hand> bustedHands() {
        return Stream.of(
                handOf(card(TEN, anonSuit()), card(TEN, anonSuit()), card(TEN, anonSuit())),
                handOf(card(TEN, anonSuit()), card(TEN, anonSuit()), card(TWO, anonSuit())),
                handOf(card(SEVEN, anonSuit()), card(SEVEN, anonSuit()), card(EIGHT, anonSuit()))
        );
    }

    private static Stream<Hand> nonBustedHands() {
        return Stream.of(
                handOf(card(TEN, anonSuit()), card(TEN, anonSuit()), card(ACE, anonSuit())),
                handOf(card(SEVEN, anonSuit()), card(SEVEN, anonSuit()), card(SIX, anonSuit())),
                handOf(card(TEN, anonSuit()), card(NINE, anonSuit()), card(TWO, anonSuit()))
        );
    }

    private static Stream<Hand> blackjackHands() {
        return Stream.of(
                handOf(card(ACE, anonSuit()), card(TEN, anonSuit())),
                handOf(card(ACE, anonSuit()), card(JACK, anonSuit())),
                handOf(card(ACE, anonSuit()), card(QUEEN, anonSuit())),
                handOf(card(ACE, anonSuit()), card(KING, anonSuit()))
        );
    }

    private static Stream<Hand> nonBlackjackHands() {
        return Stream.of(
                handOf(anonCard(), anonCard(), anonCard()),
                handOf(card(ACE, anonSuit()), card(NINE, anonSuit())),
                handOf(card(SIX, anonSuit()), card(TEN, anonSuit()))
        );
    }

    private static Stream<Map<Integer, Collection<Hand>>> scoreEvaluations() {
        final Map<Integer, Collection<Hand>> result = new HashMap<>();

        result.put(21, Stream.of(
                handOf(card(TEN, anonSuit()), card(JACK, anonSuit()), card(ACE, anonSuit())),
                handOf(card(JACK, anonSuit()), card(ACE, anonSuit())),
                handOf(card(TEN, anonSuit()), card(ACE, anonSuit()))
        ).collect(toList()));

        result.put(20, Stream.of(
                handOf(card(TEN, anonSuit()), card(TEN, anonSuit())),
                handOf(card(FIVE, anonSuit()), card(FIVE, anonSuit()), card(FIVE, anonSuit()), card(FIVE, anonSuit())),
                handOf(card(QUEEN, anonSuit()), card(JACK, anonSuit())),
                handOf(card(KING, anonSuit()), card(TEN, anonSuit())),
                handOf(card(JACK, anonSuit()), card(FIVE, anonSuit()), card(FIVE, anonSuit()))
        ).collect(toList()));

        result.put(19, Stream.of(
                handOf(card(TEN, anonSuit()), card(NINE, anonSuit())),
                handOf(card(JACK, anonSuit()), card(FIVE, anonSuit()), card(FOUR, anonSuit())),
                handOf(card(NINE, anonSuit()), card(NINE, anonSuit()), card(ACE, anonSuit()))
        ).collect(toList()));

        return Stream.of(result);
    }
}