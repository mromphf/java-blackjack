package test.domain;

import main.domain.model.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static main.domain.model.Card.card;
import static main.domain.model.Hand.handOf;
import static main.domain.model.Rank.*;
import static main.domain.function.Rules.*;
import static main.domain.model.Suit.*;
import static org.junit.jupiter.api.Assertions.*;
import static test.domain.anonymous.Anonymous.anonCard;
import static test.domain.anonymous.Anonymous.anonSuit;

public class RulesTest {

    @ParameterizedTest
    @MethodSource("blackjackHands")
    public void isBlackjack_shouldReturnTrue_whenGivenAceCardsPairedWithTensAndFaceCards(Set<Card> hand) {
        assertTrue(isBlackjack.test(hand));
    }

    @ParameterizedTest
    @MethodSource("nonBlackjackHands")
    public void isBlackjack_shouldReturnFalse_whenGivenANonBlackjackHand(Set<Card> hand) {
        assertFalse(isBlackjack.test(hand));
    }

    @Test
    public void isBust_shouldReturnTrue_whenTotalValueOfCardCollectionIsGreaterThanTwentyOne() {
        assertTrue(isBust.test(handOf(
                card(TEN, CLUBS),
                card(TEN, SPADES),
                card(TWO, CLUBS)
        )));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionIsLessThanTwentyOne() {
        assertFalse(isBust.test(handOf(
                card(FIVE, CLUBS),
                card(THREE, SPADES),
                card(TWO, CLUBS)
        )));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionHasAnAce() {
        assertFalse(isBust.test(handOf(
                new Card(ACE, HEARTS),
                new Card(JACK, CLUBS),
                new Card(KING, DIAMONDS)
        )));
    }

    @Test
    public void isPush_shouldReturnTrue_whenTotalValueOfTwoHandsIsEqual() {
        Set<Card> hand1 = new HashSet<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(JACK, CLUBS));
        }};

        List<Card> hand2 = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(FIVE, DIAMONDS));
            add(new Card(KING, CLUBS));
        }};

        assertTrue(isPush.test(hand1, hand2));
    }

    @Test
    public void isPush_shouldReturnFalse_whenTotalValueOfTwoHandsIsUnequal() {
        Queue<Card> hand1 = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(FIVE, CLUBS));
        }};

        Set<Card> hand2 = new HashSet<Card>() {{
            add(new Card(SEVEN, HEARTS));
            add(new Card(KING, CLUBS));
        }};

        assertFalse(isPush.test(hand1, hand2));
    }

    @Test
    public void score_shouldReturnTwenty_whenGivenAnAceAndANine() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(NINE, CLUBS));
        }};

        assertEquals(20, score(cards));
    }

    @Test
    public void score_shouldReturnTwentyOne_whenGivenAnAceAndKing() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(KING, CLUBS));
        }};

        assertEquals(21, score(cards));
    }

    @Test
    public void score_shouldReturnNineteen_whenGivenAnAceAnEightAndATen() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(EIGHT, SPADES));
            add(new Card(TEN, CLUBS));
        }};

        assertEquals(19, score(cards));
    }

    @Test
    public void concealedScore_shouldReturnFive_whenGivenAFiveFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(EIGHT, SPADES));
        }};

        assertEquals(5, concealedScore(cards));
    }

    @Test
    public void concealedScore_shouldReturnEleven_whenGivenAnAceFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(EIGHT, SPADES));
        }};

        assertEquals(11, concealedScore(cards));
    }

    @Test
    public void atLeastOneAce_shouldReturnTrue_whenGivenAtLeastOneAce() {
        assertTrue(atLeastOneAce.test(handOf(
                card(ACE, HEARTS),
                card(EIGHT, SPADES)
        )));
    }

    @Test
    public void atLeastOneAce_shouldReturnFalse_whenGivenNoAces() {
        assertFalse(atLeastOneAce.test(handOf(
                        card(FIVE, CLUBS),
                        card(EIGHT, DIAMONDS)
                )
        ));
    }

    @Test
    public void hardTotal_shouldReturnThirteen_whenGivenAnAceAndATwo() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TWO, SPADES));
        }};

        assertEquals(13, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyOne_whenGivenAnAceAndATen() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TEN, SPADES));
        }};

        assertEquals(21, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyFive_whenGivenAnAceAndATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TEN, SPADES));
            add(new Card(FOUR, CLUBS));
        }};

        assertEquals(25, hardTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFive_whenGivenAnAceAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(FOUR, CLUBS));
        }};

        assertEquals(5, softTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFifteen_whenGivenAnAceATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TEN, SPADES));
            add(new Card(FOUR, CLUBS));
        }};

        assertEquals(15, softTotal(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsLessThanOrEqualToTwentyOne() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(FIVE, SPADES));
            add(new Card(FIVE, SPADES));
        }};

        assertTrue(hardTotalIsFavourable.test(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsGreaterThanTwentyOne() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(SIX, SPADES));
            add(new Card(FIVE, SPADES));
        }};

        assertFalse(hardTotalIsFavourable.test(cards));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenPlayerScoreIsTwentyAndDealerScoreIsNineteen() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(FOUR, HEARTS));
            add(new Card(SIX, CLUBS));
            add(new Card(NINE, SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenDealerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(QUEEN, CLUBS));
            add(new Card(KING, SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenPlayerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(KING, SPADES));
            add(new Card(FIVE, DIAMONDS));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(NINE, SPADES));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenPlayerBothPlayerAndDealerBust() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(KING, SPADES));
            add(new Card(EIGHT, DIAMONDS));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(TEN, SPADES));
            add(new Card(TWO, SPADES));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenHandsAreOfEqualValue() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(KING, SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(FIVE, CLUBS));
            add(new Card(ACE, HEARTS));
            add(new Card(NINE, DIAMONDS));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenTwoEqualCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(FIVE, SPADES));
        }};

        assertTrue(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenATenAndAQueen() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(QUEEN, SPADES));
        }};

        assertTrue(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenTwoUnequalCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(SIX, SPADES));
        }};

        assertFalse(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenMoreThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(FIVE, SPADES));
            add(new Card(FIVE, CLUBS));
        }};

        assertFalse(canSplit.test(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenFewerThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
        }};

        assertFalse(canSplit.test(cards));
    }

    private static Stream<Set<Card>> blackjackHands() {
        return Stream.of(
                handOf(card(ACE, anonSuit()), card(TEN, anonSuit())),
                handOf(card(ACE, anonSuit()), card(JACK, anonSuit())),
                handOf(card(ACE, anonSuit()), card(QUEEN, anonSuit())),
                handOf(card(ACE, anonSuit()), card(KING, anonSuit()))
        );
    }

    private static Stream<Set<Card>> nonBlackjackHands() {
        return Stream.of(
                handOf(anonCard(), anonCard(), anonCard()),
                handOf(card(ACE, anonSuit()), card(NINE, anonSuit())),
                handOf(card(SIX, anonSuit()), card(TEN, anonSuit()))
        );
    }
}