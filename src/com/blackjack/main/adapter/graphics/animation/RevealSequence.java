package com.blackjack.main.adapter.graphics.animation;

import com.blackjack.main.adapter.graphics.Vector;
import com.blackjack.main.domain.model.Outcome;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.util.List;
import java.util.SortedMap;

import static com.blackjack.main.adapter.graphics.animation.TableDisplay.OUTCOME_COLORS;
import static com.blackjack.main.adapter.graphics.animation.TableDisplay.OUTCOME_STRINGS;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.IntStream.range;

public class RevealSequence extends AnimationTimer {

    private final static double DELAY = 500;

    private final Outcome outcome;
    private final GraphicsContext graphics;
    private final List<Image> images;
    private final SortedMap<Integer, Vector> vectors;
    private final Vector center;
    private final long START_TIME_MILLIS;

    private final static String FONT_NAME = "Arial";

    public static RevealSequence revealSequence(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
            Vector center, Outcome outcome,
            List<Image> images) {
        return new RevealSequence(graphics, vectors, center, outcome, images);
    }

    private RevealSequence(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
            Vector center, Outcome outcome,
            List<Image> images) {

        this.vectors = vectors;
        this.graphics = graphics;
        this.images = images;
        this.center = center;
        this.outcome = outcome;

        START_TIME_MILLIS = currentTimeMillis();
    }

    @Override
    public void handle(long now) {
        final long millis_elapsed = (-START_TIME_MILLIS + currentTimeMillis());

        range(0, images.size()).forEach(i -> {
            if (millis_elapsed >= (DELAY * i)) {

                final Image img = images.get(i);
                final Vector vec = vectors.get(i);

                graphics.drawImage(img, vec.x, vec.y);
            }
        });


        if (millis_elapsed > (DELAY * images.size())) {
            final Font f = new Font(FONT_NAME, 50);
            graphics.setFont(f);
            graphics.setFill(OUTCOME_COLORS.get(outcome));
            graphics.fillText(OUTCOME_STRINGS.get(outcome), center.x - 50, center.y + 50);
            stop();
        }
    }
}
