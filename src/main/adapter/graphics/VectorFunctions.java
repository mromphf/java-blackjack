package main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.LinkedList;
import java.util.List;

import static main.adapter.graphics.Vector.vector;

public class VectorFunctions {

    private static final double GAP = 0.30;
    private static final double CARD_WIDTH = 0.08;
    private static final double VER_ORIGIN_PLAYER = 0.15;
    private static final double VER_ORIGIN_DEALER = 0.15;

    public static List<Vector> vectorsDealCards(Canvas canvas) {
        final List<Vector> vectorMap = new LinkedList<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOriginPlayer = (center.y) + (canvas.getHeight() * VER_ORIGIN_PLAYER);
        double verOriginDealer = (canvas.getHeight() * VER_ORIGIN_DEALER);

        vectorMap.add(vector(horOrigin, verOriginDealer));
        vectorMap.add(vector((horOrigin + gapBetweenCards), verOriginDealer));
        vectorMap.add(vector(horOrigin, verOriginPlayer));
        vectorMap.add(vector((horOrigin + gapBetweenCards), verOriginPlayer));

        return vectorMap;
    }

    public static List<Vector> vectorsDealerReveal(Canvas canvas, int numDealerCards) {
        final List<Vector> vectorsRoot = new LinkedList<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOrigin = (canvas.getHeight() * VER_ORIGIN_DEALER);

        for (int i = 0; i <= numDealerCards; i++) {
            vectorsRoot.add(vector(horOrigin + (gapBetweenCards * i), verOrigin));
        }

        return vectorsRoot;
    }

    public static List<Vector> vectorsPlayerRow(Canvas canvas, int numDealerCards) {
        final List<Vector> vectorsRoot = new LinkedList<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOrigin = (center.y + (canvas.getHeight() * VER_ORIGIN_PLAYER));

        for (int i = 0; i <= numDealerCards; i++) {
            vectorsRoot.add(vector(horOrigin + (gapBetweenCards * i), verOrigin));
        }

        return vectorsRoot;
    }

    public static Vector center(Canvas canvas) {
        return vector((canvas.getWidth() / 2), (canvas.getHeight() / 2));
    }
}
