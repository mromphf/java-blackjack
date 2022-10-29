package com.blackjack.test.domain;

import org.junit.jupiter.api.Test;

import static com.blackjack.main.domain.model.Card.card;
import static com.blackjack.main.domain.model.Suit.*;
import static com.blackjack.main.domain.model.Rank.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    public void blackjackValue_shouldReturn10_whenCalledOnAQueen() {
        assertEquals(10, card(QUEEN, DIAMONDS).blackjackValue());
    }

    @Test
    public void isAce_shouldReturnTrue_whenGivenACardWithAValueOfOne() {
        assertTrue(card(ACE, CLUBS).isAce());
    }

    @Test
    public void isAce_shouldReturnFalse_whenGivenACardWithAValueHigherThanOne() {
        assertFalse(card(TWO, CLUBS).isAce());
    }
}