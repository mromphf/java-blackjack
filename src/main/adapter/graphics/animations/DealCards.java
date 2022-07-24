package main.adapter.graphics.animations;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.graphics.CardVector;
import main.adapter.graphics.Vector;

import java.util.*;

import static java.lang.System.currentTimeMillis;
import static main.adapter.graphics.CardVector.*;

public class DealCards extends AnimationTimer {

    private static final int DELAY_1 = 500;
    private static final int DELAY_2 = 1000;
    private static final int DELAY_3 = 1500;
    private static final int DELAY_4 = 2000;

    private final long START_TIME_MILLIS;

    private final Map<CardVector, Vector> vectorMap;

    private final GraphicsContext graphics;
    private final List<Image> images = new ArrayList<>();

    public DealCards(Map<CardVector, Vector> vectorMap,
                     GraphicsContext graphics,
                     Collection<Image> dealerCards,
                     Collection<Image> playerCards) {

        this.vectorMap = vectorMap;
        this.graphics = graphics;

        START_TIME_MILLIS = currentTimeMillis();

        Iterator<Image> dealerItr = dealerCards.iterator();
        Iterator<Image> playerItr = playerCards.iterator();

        images.add(dealerItr.next());
        images.add(dealerItr.next());
        images.add(playerItr.next());
        images.add(playerItr.next());

    }

    @Override
    public void handle(long now) {
        final long millis_elapsed = (-START_TIME_MILLIS + currentTimeMillis());

        if (millis_elapsed > DELAY_1) {
            final Vector vec = vectorMap.get(DEALER_CARD_1);
            graphics.drawImage(images.get(0), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_2) {
            final Vector vec = vectorMap.get(DEALER_CARD_2);
            graphics.drawImage(images.get(1), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_3) {
            final Vector vec = vectorMap.get(PLAYER_CARD_1);
            graphics.drawImage(images.get(2), vec.x, vec.y);
        }

        if (millis_elapsed > DELAY_4) {
            final Vector vec = vectorMap.get(PLAYER_CARD_2);
            graphics.drawImage(images.get(3), vec.x, vec.y);
            stop();
        }
    }
}
