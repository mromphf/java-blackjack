package test.adapter;

import javafx.scene.canvas.Canvas;
import main.adapter.graphics.Vector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static main.adapter.graphics.VectorFunctions.dealerReveal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorFunctionsTest {

    @Test
    public void dealerAnimation_vectorsRootSize() {
        final Canvas canvas = new Canvas((500), (500));
        final int numCards = 4;
        final int expectedLength = 3;

        final List<List<Vector>> vectorsRoot = dealerReveal(canvas, numCards);

        assertEquals(expectedLength, vectorsRoot.size());
    }
}