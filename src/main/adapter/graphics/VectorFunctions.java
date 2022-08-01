package main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.LinkedList;
import java.util.List;

import static main.adapter.graphics.Vector.vector;

public class VectorFunctions {

    public static List<Vector> vectorsDealCards(Canvas canvas) {
        final List<Vector> vectorMap = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.10);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth));
        double verOriginPlayer = (canvas.getHeight() / 2) + 110;
        double verOriginDealer = 100;

        vectorMap.add(vector(horOrigin, verOriginDealer));
        vectorMap.add(vector((horOrigin + gapBetweenCards), verOriginDealer));
        vectorMap.add(vector(horOrigin, verOriginPlayer));
        vectorMap.add(vector((horOrigin + gapBetweenCards), verOriginPlayer));

        return vectorMap;
    }

    public static List<Vector> vectorsDealerReveal(Canvas canvas, int numDealerCards) {
        final List<Vector> vectorsRoot = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.10);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth));
        double verOriginDealer = 100;

        for (int i = 0; i <= numDealerCards; i++) {
            vectorsRoot.add(vector(horOrigin + (gapBetweenCards * i), verOriginDealer));
        }

        return vectorsRoot;
    }

    public static List<Vector> vectorsPlayerRow(Canvas canvas, int numDealerCards) {
        final List<Vector> vectorsRoot = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.10);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth));
        double verOrigin = ((canvas.getHeight() / 2) + 110);

        for (int i = 0; i <= numDealerCards; i++) {
            vectorsRoot.add(vector(horOrigin + (gapBetweenCards * i), verOrigin));
        }

        return vectorsRoot;
    }

    public static Vector center(Canvas canvas) {
        return vector((canvas.getWidth() / 2), (canvas.getHeight() / 2));
    }
}
