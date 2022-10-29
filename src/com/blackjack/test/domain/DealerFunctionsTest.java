package com.blackjack.test.domain;

import com.blackjack.main.domain.model.Deck;
import org.junit.jupiter.api.Test;

import static com.blackjack.main.domain.function.DealerFunctions.freshDeck;
import static com.blackjack.main.domain.function.DealerFunctions.shuffle;
import static com.blackjack.main.domain.model.Card.card;
import static com.blackjack.main.domain.model.Deck.emptySerializedDeck;
import static com.blackjack.main.domain.model.Rank.*;
import static com.blackjack.main.domain.model.Suit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
}