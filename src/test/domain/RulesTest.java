package test.domain;

import main.domain.Card;
import main.domain.Suit;
import org.junit.jupiter.api.Test;

import java.util.*;

import static main.domain.Rank.*;
import static main.domain.Rules.*;
import static main.domain.Suit.*;
import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    @Test
    public void isBlackjack_shouldReturnTrue_whenGivenAnAceAndATen() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TEN, CLUBS));
        }};

        assertTrue(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnTrue_whenGivenAnAceAndAJack() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(JACK, CLUBS));
        }};

        assertTrue(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenAnAceAndANine() {
        List<Card> cards = new ArrayList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(NINE, CLUBS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenACollectionOfThreeCards() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(NINE, CLUBS));
            add(new Card(KING, DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenACollectionOfCardsWithoutAnAce() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(TEN, CLUBS));
            add(new Card(KING, DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBust_shouldReturnTrue_whenTotalValueOfCardCollectionIsTwentyTwo() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(TEN, CLUBS));
            add(new Card(TWO, DIAMONDS));
        }};

        assertTrue(isBust(cards));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionIsLessThanTwentyOne() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(FIVE, CLUBS));
            add(new Card(SIX, DIAMONDS));
        }};

        assertFalse(isBust(cards));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionHasAnAce() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(JACK, CLUBS));
            add(new Card(KING, DIAMONDS));
        }};

        assertFalse(isBust(cards));
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

        assertTrue(isPush(hand1, hand2));
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

        assertFalse(isPush(hand1, hand2));
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
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(EIGHT, SPADES));
        }};

        assertTrue(atLeastOneAce(cards));
    }

    @Test
    public void atLeastOneAce_shouldReturnFalse_whenGivenNoAces() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(THREE, HEARTS));
            add(new Card(EIGHT, SPADES));
        }};

        assertFalse(atLeastOneAce(cards));
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

        assertTrue(hardTotalFavorable(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsGreaterThanTwentyOne() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(SIX, SPADES));
            add(new Card(FIVE, SPADES));
        }};

        assertFalse(hardTotalFavorable(cards));
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

        assertTrue(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenATenAndAQueen() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(TEN, HEARTS));
            add(new Card(QUEEN, SPADES));
        }};

        assertTrue(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenTwoUnequalCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(SIX, SPADES));
        }};

        assertFalse(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenMoreThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(FIVE, SPADES));
            add(new Card(FIVE, CLUBS));
        }};

        assertFalse(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenFewerThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FIVE, HEARTS));
        }};

        assertFalse(canSplit(cards));
    }

    @Test
    public void insuranceAvailable_shouldReturnTrue_whenTheFirstCardIsAnAce() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(ACE, HEARTS));
            add(new Card(FOUR, DIAMONDS));
        }};

        assertTrue(insuranceAvailable(cards));
    }

    @Test
    public void insuranceAvailable_shouldReturnFalse_whenTheSecondCardIsAnAceButTheFirstIsNot() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FOUR, HEARTS));
            add(new Card(ACE, DIAMONDS));
        }};

        assertFalse(insuranceAvailable(cards));
    }

    @Test
    public void insuranceAvailable_shouldReturnFalse_whenNoAces() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(FOUR, HEARTS));
            add(new Card(SEVEN, DIAMONDS));
        }};

        assertFalse(insuranceAvailable(cards));
    }

    @Test
    public void insuranceAvailable_shouldReturnFalse_whenGivenEmptyCollection() {
        List<Card> cards = new LinkedList<>();

        assertFalse(insuranceAvailable(cards));
    }
}