package main.adapter.graphics;

import javafx.scene.canvas.Canvas;

import java.util.*;

import static main.adapter.graphics.CardVector.*;
import static main.adapter.graphics.Vector.vector;

public class VectorFunctions {

    public static Map<CardVector, Vector> openingCardDeal(Canvas canvas) {
        final Map<CardVector, Vector> vectorMap = new HashMap<>();

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
        final List<List<Vector>> vectors = new LinkedList<>();

        double gapBetweenCards = (canvas.getWidth() * 0.15);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth * 2));
        double verOriginPlayer = (canvas.getHeight() / 2) + 110;
        double verOriginDealer = 100;

        for (int i = 0; i < numDealerCards; i++) {

        }

        return vectors;
    }
}
