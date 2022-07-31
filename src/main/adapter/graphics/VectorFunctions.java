package main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static main.adapter.graphics.Vector.vector;
import static main.adapter.graphics.VectorName.*;

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

    public static List<Vector> dealerReveal(Canvas canvas, int numDealerCards) {
        final List<Vector> vectorsRoot = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.10);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth * 2));
        double verOriginDealer = 100;

        for (int i = 0; i <= numDealerCards; i++) {
            vectorsRoot.add(vector(horOrigin + (gapBetweenCards * i), verOriginDealer));
        }

        return vectorsRoot;
    }
}
