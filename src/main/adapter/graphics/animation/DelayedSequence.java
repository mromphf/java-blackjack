package main.adapter.graphics.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.graphics.Vector;

import java.util.List;

import static java.lang.System.currentTimeMillis;

public class DelayedSequence extends AnimationTimer {

    private final long START_TIME_MILLIS;

    private final List<Vector> vectors;
    private final GraphicsContext graphics;
    private final List<Image> images;

    public static DelayedSequence delayedSequence(GraphicsContext graphics,
                           List<Vector> vectors,
                           List<Image> images) {
        return new DelayedSequence(graphics, vectors, images);
    }

    private DelayedSequence(GraphicsContext graphics,
                           List<Vector> vectors,
                           List<Image> images) {

        this.vectors = vectors;
        this.graphics = graphics;
        this.images = images;

        START_TIME_MILLIS = currentTimeMillis();
    }

    @Override
    public void handle(long now) {
        final long millis_elapsed = (-START_TIME_MILLIS + currentTimeMillis());

        for (int i = 0; i < images.size(); i++) {
            if (millis_elapsed >= (500d * i)) {

                final Image img = images.get(i);
                final Vector vec = vectors.get(i);

                graphics.drawImage(img, vec.x, vec.y);
            }

        }

        if (millis_elapsed > (500d * images.size())) {
            stop();
        }
    }
}
