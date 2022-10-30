package com.blackjack.main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.SortedMap;
import java.util.TreeMap;

import static com.blackjack.main.adapter.graphics.Vector.vector;

public class VectorFunctions {

    private static final double GAP = 0.30;
    private static final double CARD_WIDTH = 0.08;
    private static final double VER_ORIGIN_PLAYER = 0.15;
    private static final double VER_ORIGIN_DEALER = 0.15;

    public static SortedMap<Integer, Vector> vectorsDealCards(Canvas canvas) {
        final SortedMap<Integer, Vector> vectorMap = new TreeMap<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOriginPlayer = (center.y) + (canvas.getHeight() * VER_ORIGIN_PLAYER);
        double verOriginDealer = (canvas.getHeight() * VER_ORIGIN_DEALER);

        vectorMap.put(0, vector(horOrigin, verOriginDealer));
        vectorMap.put(1, vector((horOrigin + gapBetweenCards), verOriginDealer));
        vectorMap.put(2, vector(horOrigin, verOriginPlayer));
        vectorMap.put(3, vector((horOrigin + gapBetweenCards), verOriginPlayer));

        return vectorMap;
    }

    public static SortedMap<Integer, Vector> vectorsDealerReveal(Canvas canvas, int numCards) {
        final SortedMap<Integer, Vector> vectorsRoot = new TreeMap<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOrigin = (canvas.getHeight() * VER_ORIGIN_DEALER);

        for (int i = 0; i <= numCards; i++) {
            vectorsRoot.put(i, vector(horOrigin + (gapBetweenCards * i), verOrigin));
        }

        return vectorsRoot;
    }

    public static SortedMap<Integer, Vector> vectorsPlayerRow(Canvas canvas, int numCards) {
        final SortedMap<Integer, Vector> vectorsRoot = new TreeMap<>();
        final Vector center = center(canvas);

        double cardWidth = (canvas.getWidth() * CARD_WIDTH);
        double gapBetweenCards = (cardWidth * GAP);
        double horOrigin = (center.x - cardWidth);
        double verOrigin = (center.y + (canvas.getHeight() * VER_ORIGIN_PLAYER));

        for (int i = 0; i <= numCards; i++) {
            vectorsRoot.put(i, (vector(horOrigin + (gapBetweenCards * i), verOrigin)));
        }

        return vectorsRoot;
    }

    public static Vector center(Canvas canvas) {
        return vector((canvas.getWidth() / 2), (canvas.getHeight() / 2));
    }
}
