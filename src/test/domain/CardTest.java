package test.domain;

import main.domain.Card;
import main.domain.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardTest {

    @Test
    public void constructor_shouldThrowIllegalArgumentException_whenGivenAValueHigherThanThirteen() {
        assertThrows(IllegalArgumentException.class, () -> new Card(14, Suit.SPADES));
    }

    @Test
    public void constructor_shouldThrowIllegalArgumentException_whenGivenAValueLowerThanOne() {
        assertThrows(IllegalArgumentException.class, () -> new Card(0, Suit.SPADES));
    }

    @Test
    public void faceValue_shouldReturn12_whenCalledOnAQueen() {
        final Card queenCard = new Card(12, Suit.DIAMONDS);

        assertEquals(12, queenCard.getFaceValue());
    }

    @Test
    public void blackjackValue_shouldReturn10_whenCalledOnAQueen() {
        final Card queenCard = new Card(12, Suit.DIAMONDS);

        assertEquals(10, queenCard.getBlackjackValue());
    }

    @Test
    public void equals_shouldReturnTrue_whenComparingTwoCardsOfEqualValue() {
        final Card card1 = new Card(7, Suit.CLUBS);
        final Card card2 = new Card(7, Suit.CLUBS);

        assertEquals(card1, card2);
    }

    @Test
    public void equals_shouldReturnFalse_whenComparingTwoCardsOfUnequalsValue() {
        final Card card1 = new Card(7, Suit.CLUBS);
        final Card card2 = new Card(5, Suit.SPADES);

        assertNotEquals(card2, card1);
    }

    @Test
    public void isAce_shouldReturnTrue_whenGivenACardWithAValueOfOne() {
        final Card card = new Card(1, Suit.CLUBS);

        assertTrue(card.isAce());
    }

    @Test
    public void isAce_shouldReturnFalse_whenGivenACardWithAValueHigherThanOne() {
        final Card card = new Card(2, Suit.CLUBS);

        assertFalse(card.isAce());
    }
}