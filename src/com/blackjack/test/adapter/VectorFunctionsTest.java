package com.blackjack.test.adapter;

import javafx.scene.canvas.Canvas;
import com.blackjack.main.adapter.graphics.Vector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.blackjack.main.adapter.graphics.VectorFunctions.vectorsDealerReveal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorFunctionsTest {

    private final static Canvas TEST_CANVAS = new Canvas((500), (500));

    @Test
    public void dealerReveal_vectorRootsSize() {
        final int numCards = 4;
        final int expectedLength = 3;

        final List<Vector> vectorsRoot = vectorsDealerReveal(TEST_CANVAS, numCards);

        assertEquals(expectedLength, vectorsRoot.size());
    }
}