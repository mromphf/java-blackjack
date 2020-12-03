package test.domain;

import main.domain.Card;
import main.domain.Suit;
import org.junit.jupiter.api.Test;

import java.util.*;

import static main.domain.Rules.*;
import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    @Test
    public void isBlackjack_shouldReturnTrue_whenGivenAnAceAndATen() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.CLUBS));
        }};

        assertTrue(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnTrue_whenGivenAnAceAndAJack() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(11, Suit.CLUBS));
        }};

        assertTrue(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenAnAceAndANine() {
        List<Card> cards = new ArrayList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(9, Suit.CLUBS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenACollectionOfThreeCards() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(9, Suit.CLUBS));
            add(new Card(13, Suit.DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenACollectionOfCardsWithoutAnAce() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.CLUBS));
            add(new Card(13, Suit.DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBust_shouldReturnTrue_whenTotalValueOfCardCollectionIsTwentyTwo() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(10, Suit.CLUBS));
            add(new Card(2, Suit.DIAMONDS));
        }};

        assertTrue(isBust(cards));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionIsLessThanTwentyOne() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(5, Suit.CLUBS));
            add(new Card(6, Suit.DIAMONDS));
        }};

        assertFalse(isBust(cards));
    }

    @Test
    public void isBust_shouldReturnFalse_whenTotalValueOfCardCollectionHasAnAce() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(11, Suit.CLUBS));
            add(new Card(13, Suit.DIAMONDS));
        }};

        assertFalse(isBust(cards));
    }

    @Test
    public void isPush_shouldReturnTrue_whenTotalValueOfTwoHandsIsEqual() {
        Set<Card> hand1 = new HashSet<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(11, Suit.CLUBS));
        }};

        List<Card> hand2 = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(5, Suit.DIAMONDS));
            add(new Card(13, Suit.CLUBS));
        }};

        assertTrue(isPush(hand1, hand2));
    }

    @Test
    public void isPush_shouldReturnFalse_whenTotalValueOfTwoHandsIsUnequal() {
        Queue<Card> hand1 = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(5, Suit.CLUBS));
        }};

        Set<Card> hand2 = new HashSet<Card>() {{
            add(new Card(7, Suit.HEARTS));
            add(new Card(13, Suit.CLUBS));
        }};

        assertFalse(isPush(hand1, hand2));
    }

    @Test
    public void score_shouldReturnTwenty_whenGivenAnAceAndANine() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(9, Suit.CLUBS));
        }};

        assertEquals(20, score(cards));
    }

    @Test
    public void score_shouldReturnTwentyOne_whenGivenAnAceAndKing() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(13, Suit.CLUBS));
        }};

        assertEquals(21, score(cards));
    }

    @Test
    public void score_shouldReturnNineteen_whenGivenAnAceAnEightAndATen() {
        Set<Card> cards = new HashSet<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(8, Suit.SPADES));
            add(new Card(10, Suit.CLUBS));
        }};

        assertEquals(19, score(cards));
    }

    @Test
    public void concealedScore_shouldReturnFive_whenGivenAFiveFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(8, Suit.SPADES));
        }};

        assertEquals(5, concealedScore(cards));
    }

    @Test
    public void concealedScore_shouldReturnEleven_whenGivenAnAceFirst() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(8, Suit.SPADES));
        }};

        assertEquals(11, concealedScore(cards));
    }

    @Test
    public void atLeastOneAce_shouldReturnTrue_whenGivenAtLeastOneAce() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(8, Suit.SPADES));
        }};

        assertTrue(atLeastOneAce(cards));
    }

    @Test
    public void atLeastOneAce_shouldReturnFalse_whenGivenNoAces() {
        Queue<Card> cards = new LinkedList<Card>() {{
            add(new Card(3, Suit.HEARTS));
            add(new Card(8, Suit.SPADES));
        }};

        assertFalse(atLeastOneAce(cards));
    }

    @Test
    public void hardTotal_shouldReturnThirteen_whenGivenAnAceAndATwo() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(2, Suit.SPADES));
        }};

        assertEquals(13, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyOne_whenGivenAnAceAndATen() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.SPADES));
        }};

        assertEquals(21, hardTotal(cards));
    }

    @Test
    public void hardTotal_shouldReturnTwentyFive_whenGivenAnAceAndATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.SPADES));
            add(new Card(4, Suit.CLUBS));
        }};

        assertEquals(25, hardTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFive_whenGivenAnAceAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(4, Suit.CLUBS));
        }};

        assertEquals(5, softTotal(cards));
    }

    @Test
    public void softTotal_shouldReturnFifteen_whenGivenAnAceATenAndAFour() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.SPADES));
            add(new Card(4, Suit.CLUBS));
        }};

        assertEquals(15, softTotal(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsLessThanOrEqualToTwentyOne() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(5, Suit.SPADES));
            add(new Card(5, Suit.SPADES));
        }};

        assertTrue(hardTotalFavorable(cards));
    }

    @Test
    public void hardTotalFavorable_shouldReturnTrue_whenTheValueOfTheHardTotalIsGreaterThanTwentyOne() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(6, Suit.SPADES));
            add(new Card(5, Suit.SPADES));
        }};

        assertFalse(hardTotalFavorable(cards));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenPlayerScoreIsTwentyAndDealerScoreIsNineteen() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(13, Suit.SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(4, Suit.HEARTS));
            add(new Card(6, Suit.CLUBS));
            add(new Card(9, Suit.SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnTrue_whenDealerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(13, Suit.SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(12, Suit.CLUBS));
            add(new Card(13, Suit.SPADES));
        }};

        assertTrue(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenPlayerBusts() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(13, Suit.SPADES));
            add(new Card(5, Suit.DIAMONDS));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(9, Suit.SPADES));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void playerWins_shouldReturnFalse_whenHandsAreOfEqualValue() {
        List<Card> playerHand = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(13, Suit.SPADES));
        }};

        List<Card> dealerHand = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(5, Suit.CLUBS));
            add(new Card(1, Suit.HEARTS));
            add(new Card(9, Suit.DIAMONDS));
        }};

        assertFalse(playerWins(playerHand, dealerHand));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenTwoEqualCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(5, Suit.SPADES));
        }};

        assertTrue(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnTrue_whenGivenATenAndAQueen() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(12, Suit.SPADES));
        }};

        assertTrue(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenTwoUnequalCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(6, Suit.SPADES));
        }};

        assertFalse(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenMoreThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(5, Suit.SPADES));
            add(new Card(5, Suit.CLUBS));
        }};

        assertFalse(canSplit(cards));
    }

    @Test
    public void canSplit_shouldReturnFalse_whenGivenFewerThanTwoCards() {
        List<Card> cards = new LinkedList<Card>() {{
            add(new Card(5, Suit.HEARTS));
        }};

        assertFalse(canSplit(cards));
    }
}