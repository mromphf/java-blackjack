package main.adapter.graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.*;

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

    public static List<List<Map<Vector, Image>>> associate(
            List<List<Vector>> vectorsRoot,
            Collection<Image> imageCollection) {

        final List<List<Map<Vector, Image>>> associations = new ArrayList<>();

        for (List<Vector> vectors : vectorsRoot) {
            final List<Map<Vector, Image>> workingAssociations = new ArrayList<>();
            final Map<Vector, Image> innerAssociations = new HashMap<>();
            final Iterator<Image> images = imageCollection.iterator();

            for (final Vector vec : vectors) {
                final Image img = images.next();

                innerAssociations.put(vec, img);
            }

            workingAssociations.add(innerAssociations);
            associations.add(workingAssociations);
        }

        return associations;
    }
}
