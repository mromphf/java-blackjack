package test.domain;

import main.domain.Card;
import main.domain.Deck;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static main.domain.function.Dealer.*;
import static main.domain.Rank.*;
import static main.domain.Suit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DealerTest {

    private static final int EXPECTED_CARDS_OF_SUIT = 13;

    @Test
    public void fresh_shouldProvideAStackOf52Cards() {
        assertEquals(52, freshDeck().size());
    }

    @Test
    public void fresh_shouldContain13HeartCards() {
        final Deck deck = freshDeck();

        final int numberHeartCards = (int) deck.stream()
                .filter(card -> card.is(HEARTS))
                .count();

        assertEquals(EXPECTED_CARDS_OF_SUIT, numberHeartCards);
    }

    @Test
    public void fresh_shouldContain13ClubCards() {
        final Deck deck = freshDeck();

        final int numberDiamondCards = (int) deck.stream()
                .filter(card -> card.is(CLUBS))
                .count();

        assertEquals(EXPECTED_CARDS_OF_SUIT, numberDiamondCards);
    }

    @Test
    public void fresh_shouldContain13DiamondCards() {
        final Deck deck = freshDeck();

        final int numDiamondCards = (int) deck.stream()
                .filter(card -> card.is(DIAMONDS))
                .count();

        assertEquals(EXPECTED_CARDS_OF_SUIT, numDiamondCards);
    }

    @Test
    public void fresh_shouldContain13SpadeCards() {
        final Deck deck = freshDeck();

        final int numSpadeCards = (int) deck.stream()
                .filter(card -> card.is(SPADES))
                .count();

        assertEquals(EXPECTED_CARDS_OF_SUIT, numSpadeCards);
    }

    @Test
    public void shuffle_shouldContain3Cards_whenPassedACollectionOf3Cards() {
        Deck cards = new Deck() {{
           add(new Card(FIVE, HEARTS));
            add(new Card(THREE, SPADES));
            add(new Card(QUEEN, CLUBS));
        }};

        assertEquals(3, shuffle(cards).size());
    }

    @Test
    public void shuffle_shouldContainTheSameCardAsTheStackPassedIn() {
        final Card card = new Card(FIVE, HEARTS);

        Deck cards = new Deck() {{
            add(card);
            add(new Card(THREE, SPADES));
            add(new Card(QUEEN, CLUBS));
        }};

        assertTrue(shuffle(cards).contains(card));
    }

    @Test
    public void openingHand_shouldThrowIllegalArgumentException_whenGivenACollectionOfFewerThanFourCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(THREE, SPADES));
            add(new Card(QUEEN, CLUBS));
        }};

        assertThrows(IllegalArgumentException.class, () -> openingHand(cards));
    }

    @Test
    public void openingHand_shouldRemoveFourCards_whenGivenAStackOfMoreThanThreeCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(new Card(FIVE, HEARTS));
            add(new Card(THREE, SPADES));
            add(new Card(QUEEN, CLUBS));
            add(new Card(ACE, CLUBS));
        }};

        openingHand(cards);

        assertEquals(0, cards.size());
    }
}