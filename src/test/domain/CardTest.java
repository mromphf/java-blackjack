package test.domain;

import main.domain.Card;
import org.junit.jupiter.api.Test;

import static main.domain.Suit.*;
import static main.domain.Rank.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    public void faceValue_shouldReturn12_whenCalledOnAQueen() {
        final Card queenCard = new Card(QUEEN, DIAMONDS);

        assertEquals(12, queenCard.getRank().VALUE);
    }

    @Test
    public void blackjackValue_shouldReturn10_whenCalledOnAQueen() {
        final Card queenCard = new Card(QUEEN, DIAMONDS);

        assertEquals(10, queenCard.getBlackjackValue());
    }

    @Test
    public void isAce_shouldReturnTrue_whenGivenACardWithAValueOfOne() {
        final Card card = new Card(ACE, CLUBS);

        assertTrue(card.isAce());
    }

    @Test
    public void isAce_shouldReturnFalse_whenGivenACardWithAValueHigherThanOne() {
        final Card card = new Card(TWO, CLUBS);

        assertFalse(card.isAce());
    }
}