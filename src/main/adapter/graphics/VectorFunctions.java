package main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.*;

import static main.adapter.graphics.VectorName.*;
import static main.adapter.graphics.Vector.vector;

public class VectorFunctions {

    public static Map<VectorName, Vector> openingCardDeal(Canvas canvas) {
        final Map<VectorName, Vector> vectorMap = new HashMap<>();

        double gapBetweenCards = (canvas.getWidth() * 0.15);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth * 2));
        double verOriginPlayer = (canvas.getHeight() / 2) + 110;
        double verOriginDealer = 100;

        vectorMap.put(DEALER_CARD_1, vector(horOrigin, verOriginDealer));
        vectorMap.put(DEALER_CARD_2, vector((horOrigin + gapBetweenCards), verOriginDealer));
        vectorMap.put(PLAYER_CARD_1, vector(horOrigin, verOriginPlayer));
        vectorMap.put(PLAYER_CARD_2, vector((horOrigin + gapBetweenCards), verOriginPlayer));

        return vectorMap;
    }

    public static List<List<Vector>> dealerReveal(Canvas canvas, int numDealerCards) {
        final List<List<Vector>> vectorsRoot = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.15);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth * 2));
        double verOriginDealer = 100;

        for (int i = 2; i <= numDealerCards; i++) {
            List<Vector> vectorsInner = new LinkedList<>();

            for (int j = 0; j < i; j++) {
                vectorsInner.add(vector(horOrigin + (gapBetweenCards * j), verOriginDealer));
            }

            vectorsRoot.add(vectorsInner);
        }

        return vectorsRoot;
    }
}