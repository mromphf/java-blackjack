package com.blackjack.test.domain;

import com.blackjack.main.domain.model.Card;
import com.blackjack.main.domain.model.Deck;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static com.blackjack.main.domain.function.DealerFunctions.*;
import static com.blackjack.main.domain.model.Card.card;
import static com.blackjack.main.domain.model.Deck.emptySerializedDeck;
import static com.blackjack.main.domain.model.Rank.*;
import static com.blackjack.main.domain.model.Suit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DealerFunctionsTest {

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
        final Deck cards = emptySerializedDeck();
        cards.add(card(FIVE, HEARTS));
        cards.add(card(THREE, SPADES));
        cards.add(card(QUEEN, CLUBS));

        assertEquals(3, shuffle(cards).size());
    }

    @Test
    public void openingHand_shouldThrowIllegalArgumentException_whenGivenACollectionOfFewerThanFourCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(THREE, SPADES));
            add(card(QUEEN, CLUBS));
        }};

        assertThrows(IllegalArgumentException.class, () -> openingHand(cards));
    }

    @Test
    public void openingHand_shouldRemoveFourCards_whenGivenAStackOfMoreThanThreeCards() {
        Stack<Card> cards = new Stack<Card>() {{
            add(card(FIVE, HEARTS));
            add(card(THREE, SPADES));
            add(card(QUEEN, CLUBS));
            add(card(ACE, CLUBS));
        }};

        openingHand(cards);

        assertEquals(0, cards.size());
    }
}