package com.blackjack.main.adapter.graphics.animation;

import com.blackjack.main.adapter.graphics.Vector;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;
import java.util.SortedMap;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.IntStream.range;

public class RevealSequence extends AnimationTimer {

    private final static double DELAY = 500;
    private final GraphicsContext graphics;
    private final List<Image> images;
    private final SortedMap<Integer, Vector> vectors;
    private final long START_TIME_MILLIS;
    private final Callback callback;

    public static RevealSequence revealSequence(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
            List<Image> images, Callback callback) {
        return new RevealSequence(graphics, vectors, images, callback);
    }

    private RevealSequence(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
            List<Image> images, Callback callback) {

        this.vectors = vectors;
        this.graphics = graphics;
        this.images = images;
        this.callback = callback;

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
            stop();
            callback.call();
        }
    }
}
