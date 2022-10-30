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
    private final double gapBetweenCards;
    private final double horOrigin;
    private final double verOriginPlayer;
    private final double verOriginDealer;

    public VectorFunctions(Canvas canvas) {
        final Vector center = center(canvas);
        final double cardWidth = (canvas.getWidth() * CARD_WIDTH);

        this.gapBetweenCards = (cardWidth * GAP);
        this.horOrigin = (center.x - cardWidth);
        this.verOriginPlayer = (center.y) + (canvas.getHeight() * VER_ORIGIN_PLAYER);
        this.verOriginDealer = (canvas.getHeight() * VER_ORIGIN_DEALER);
    }

    public SortedMap<Integer, Vector> deal() {
        final SortedMap<Integer, Vector> vectorMap = new TreeMap<>();

        vectorMap.put(0, vector(horOrigin, verOriginPlayer));
        vectorMap.put(1, vector(horOrigin, verOriginDealer));
        vectorMap.put(2, vector((horOrigin + gapBetweenCards), verOriginPlayer));
        vectorMap.put(3, vector((horOrigin + gapBetweenCards), verOriginDealer));

        return vectorMap;
    }

    public SortedMap<Integer, Vector> dealer(int numCards) {
        final SortedMap<Integer, Vector> vectorsRoot = new TreeMap<>();

        for (int i = 0; i <= numCards; i++) {
            vectorsRoot.put(i, vector(horOrigin + (gapBetweenCards * i), verOriginDealer));
        }

        return vectorsRoot;
    }

    public SortedMap<Integer, Vector> player(int numCards) {
        final SortedMap<Integer, Vector> vectorsRoot = new TreeMap<>();

        for (int i = 0; i <= numCards; i++) {
            vectorsRoot.put(i, (vector(horOrigin + (gapBetweenCards * i), verOriginPlayer)));
        }

        return vectorsRoot;
    }

    public static Vector center(Canvas canvas) {
        return vector((canvas.getWidth() / 2), (canvas.getHeight() / 2));
    }
}
