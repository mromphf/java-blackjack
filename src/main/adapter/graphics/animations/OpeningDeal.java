package main.adapter.graphics.animations;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static javafx.stage.Screen.getPrimary;

public class OpeningDeal extends AnimationTimer {

    private static final int DELAY_1 = 500;
    private static final int DELAY_2 = 1000;
    private static final int DELAY_3 = 1500;
    private static final int DELAY_4 = 2000;

    private final double GAP_BETWEEN_CARDS;
    private final double START_HOR;
    private final double START_VER;
    private final long START_TIME_MILLIS;

    private final GraphicsContext graphics;
    private final List<Image> images = new ArrayList<>();

    public OpeningDeal(Canvas canvas,
                       Collection<Image> dealerCards,
                       Collection<Image> playerCards) {
        this.graphics = canvas.getGraphicsContext2D();

        final Rectangle2D screen = getPrimary().getBounds();

        Iterator<Image> dealerItr = dealerCards.iterator();
        Iterator<Image> playerItr = playerCards.iterator();

        images.add(dealerItr.next());
        images.add(dealerItr.next());
        images.add(playerItr.next());
        images.add(playerItr.next());

        START_TIME_MILLIS = currentTimeMillis();

        double HOR_CENTER = canvas.getWidth() / 2;
        double VER_CENTER = canvas.getHeight() / 2;
        double CARD_WIDTH = screen.getWidth() * 0.08;

        START_HOR = HOR_CENTER - (CARD_WIDTH * images.size()) + (CARD_WIDTH / 2);
        START_VER = VER_CENTER + 110;
        GAP_BETWEEN_CARDS = screen.getWidth() * 0.15;
    }

    @Override
    public void handle(long now) {
        final long currTimeMillis = currentTimeMillis();
        final long millis_elapsed = -START_TIME_MILLIS + currTimeMillis;

        if (millis_elapsed > DELAY_1) {
            graphics.drawImage(images.get(0), START_HOR + GAP_BETWEEN_CARDS, 100);
        }

        if (millis_elapsed > DELAY_2) {
            graphics.drawImage(images.get(1), START_HOR + (2 * GAP_BETWEEN_CARDS), 100);
        }

        if (millis_elapsed > DELAY_3) {
            graphics.drawImage(images.get(2), START_HOR + GAP_BETWEEN_CARDS, START_VER);
        }

        if (millis_elapsed > DELAY_4) {
            graphics.drawImage(images.get(3), START_HOR + (2 * GAP_BETWEEN_CARDS), START_VER);
            stop();
        }
    }
}
