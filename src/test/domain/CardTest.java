package test.domain;

import org.junit.jupiter.api.Test;

import static main.domain.Card.card;
import static main.domain.Suit.*;
import static main.domain.Rank.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    public void blackjackValue_shouldReturn10_whenCalledOnAQueen() {
        assertEquals(10, card(QUEEN, DIAMONDS).getBlackjackValue());
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