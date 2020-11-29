package test.domain;

import main.domain.Card;
import main.domain.Suit;
import org.junit.jupiter.api.Test;

import java.util.*;

import static main.domain.Rules.isBust;
import static main.domain.Rules.isBlackjack;
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
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(9, Suit.CLUBS));
            add(new Card(13, Suit.DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBlackjack_shouldReturnFalse_whenGivenACollectionOfCardsWithoutAnAce() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(1, Suit.HEARTS));
            add(new Card(10, Suit.CLUBS));
            add(new Card(13, Suit.DIAMONDS));
        }};

        assertFalse(isBlackjack(cards));
    }

    @Test
    public void isBust_shouldReturnTrue_whenTotalValueOfCardCollectionIsHigherThanTwentyOne() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(10, Suit.HEARTS));
            add(new Card(10, Suit.CLUBS));
            add(new Card(2, Suit.DIAMONDS));
        }};

        assertTrue(isBust(cards));
    }
}