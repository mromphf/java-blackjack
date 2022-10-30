package com.blackjack.test.adapter;

import com.blackjack.main.adapter.graphics.Vector;
import com.blackjack.main.adapter.graphics.VectorFunctions;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorFunctionsTest {

    private final static Canvas TEST_CANVAS = new Canvas((500), (500));

    @Test
    public void dealerReveal_vectorRootsSize() {
        final int numCards = 4;
        final int expectedLength = 3;

        final VectorFunctions vectorFunctions = new VectorFunctions(TEST_CANVAS);

        final SortedMap<Integer, Vector> vectorsRoot = vectorFunctions.dealer(numCards);

        assertEquals(expectedLength, vectorsRoot.size());
    }
}