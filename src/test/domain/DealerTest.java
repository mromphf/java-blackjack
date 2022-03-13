package test.domain;

import main.domain.Card;
import main.domain.Suit;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static main.domain.Dealer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DealerTest {

    @Test
    public void fresh_shouldProvideAStackOf52Cards() {
        assertEquals(52, freshDeck().size());
    }

    @Test
    public void fresh_shouldContain13HeartCards() {
        Stack<Card> deck = freshDeck();

        final int numCards = (int) deck.stream().filter(c -> c.getSuit().equals(Suit.HEARTS)).count();

        assertEquals(13, numCards);
    }

    @Test
    public void fresh_shouldContain13ClubCards() {
        Stack<Card> deck = freshDeck();

        final int numCards = (int) deck.stream().filter(c -> c.getSuit().equals(Suit.CLUBS)).count();

        assertEquals(13, numCards);
    }

    @Test
    public void fresh_shouldContain13DiamondCards() {
        Stack<Card> deck = freshDeck();

        final int numCards = (int) deck.stream().filter(c -> c.getSuit().equals(Suit.DIAMONDS)).count();

        assertEquals(13, numCards);
    }

    @Test
    public void fresh_shouldContain13SpadeCards() {
        Stack<Card> deck = freshDeck();

        final int numCards = (int) deck.stream().filter(c -> c.getSuit().equals(Suit.SPADES)).count();

        assertEquals(13, numCards);
    }

    @Test
    public void shuffle_shouldContain3Cards_whenPassedACollectionOf3Cards() {
        Stack<Card> cards = new Stack<Card>() {{
           add(new Card(5, Suit.HEARTS));
            add(new Card(3, Suit.SPADES));
            add(new Card(12, Suit.CLUBS));
        }};

        assertEquals(3, shuffle(cards).size());
    }

    @Test
    public void shuffle_shouldContainTheSameCardAsTheStackPassedIn() {
        final Card card = new Card(5, Suit.HEARTS);

        Stack<Card> cards = new Stack<Card>() {{
            add(card);
            add(new Card(3, Suit.SPADES));
            add(new Card(12, Suit.CLUBS));
        }};

        assertTrue(shuffle(cards).contains(card));
    }

    @Test
    public void openingHand_shouldThrowIllegalArgumentException_whenGivenACollectionOfFewerThanFourCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(3, Suit.SPADES));
            add(new Card(12, Suit.CLUBS));
        }};

        assertThrows(IllegalArgumentException.class, () -> openingHand(cards));
    }

    @Test
    public void openingHand_shouldRemoveFourCards_whenGivenAStackOfMoreThanThreeCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(5, Suit.HEARTS));
            add(new Card(3, Suit.SPADES));
            add(new Card(12, Suit.CLUBS));
            add(new Card(1, Suit.CLUBS));
        }};

        openingHand(cards);

        assertEquals(0, cards.size());
    }
}