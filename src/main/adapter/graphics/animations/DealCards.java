package main.adapter.graphics.animations;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.graphics.Vector;

import java.util.*;

import static java.lang.System.currentTimeMillis;
import static main.adapter.graphics.Vector.vector;
import static main.adapter.graphics.animations.DealCards.CardVector.*;

public class DealCards extends AnimationTimer {

    private static final int DELAY_1 = 500;
    private static final int DELAY_2 = 1000;
    private static final int DELAY_3 = 1500;
    private static final int DELAY_4 = 2000;

    private final long START_TIME_MILLIS;

    private final Map<CardVector, Vector> cardVectors = new HashMap<>();

    private final GraphicsContext graphics;
    private final List<Image> images = new ArrayList<>();

    public DealCards(Canvas canvas,
                     Collection<Image> dealerCards,
                     Collection<Image> playerCards) {

        this.graphics = canvas.getGraphicsContext2D();

        double gapBetweenCards = (canvas.getWidth() * 0.15);
        double cardWidth = (canvas.getWidth() * 0.08);
        double horOrigin = ((canvas.getWidth() / 2) - (cardWidth * images.size()) - gapBetweenCards);
        double verOriginPlayer = (canvas.getHeight() / 2) + 110;
        double verOriginDealer = 100;

        START_TIME_MILLIS = currentTimeMillis();

        Iterator<Image> dealerItr = dealerCards.iterator();
        Iterator<Image> playerItr = playerCards.iterator();

        images.add(dealerItr.next());
        images.add(dealerItr.next());
        images.add(playerItr.next());
        images.add(playerItr.next());

        cardVectors.put(DEALER_CARD_1, vector(horOrigin, verOriginDealer));
        cardVectors.put(DEALER_CARD_2, vector((horOrigin + gapBetweenCards), verOriginDealer));
        cardVectors.put(PLAYER_CARD_1, vector(horOrigin, verOriginPlayer));
        cardVectors.put(PLAYER_CARD_2, vector((horOrigin + gapBetweenCards), verOriginPlayer));
    }

    @Override
    public void handle(long now) {
        final long currTimeMillis = currentTimeMillis();
        final long millis_elapsed = (-START_TIME_MILLIS + currTimeMillis);

        if (millis_elapsed > DELAY_1) {
            final Vector vec = cardVectors.get(DEALER_CARD_1);
            graphics.drawImage(images.get(0), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_2) {
            final Vector vec = cardVectors.get(DEALER_CARD_2);
            graphics.drawImage(images.get(1), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_3) {
            final Vector vec = cardVectors.get(PLAYER_CARD_1);
            graphics.drawImage(images.get(2), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_4) {
            final Vector vec = cardVectors.get(PLAYER_CARD_2);
            graphics.drawImage(images.get(3), vec.x, vec.y);
            stop();
        }
    }

    public enum CardVector {
        DEALER_CARD_1,
        DEALER_CARD_2,
        PLAYER_CARD_1,
        PLAYER_CARD_2
    }
}
