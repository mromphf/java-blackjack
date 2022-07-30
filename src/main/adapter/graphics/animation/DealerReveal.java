package main.adapter.graphics.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.graphics.Vector;

import java.util.List;

import static java.lang.System.currentTimeMillis;

public class DealerReveal extends AnimationTimer {

    private final long START_TIME_MILLIS;

    private final List<Image> images;
    private final List<List<Vector>> vectorsRoot;
    private final GraphicsContext graphics;

    public DealerReveal(GraphicsContext graphics, List<List<Vector>> vectorsRoot, List<Image> images) {
        this.graphics = graphics;
        this.vectorsRoot = vectorsRoot;
        this.images = images;

        START_TIME_MILLIS = currentTimeMillis();
    }

    @Override
    public void handle(long now) {
        final long millis_elapsed = (-START_TIME_MILLIS + currentTimeMillis());

        for (int i = 0; i < images.size(); i++) {
            if (millis_elapsed >= (500d * i)) {
                final List<Vector> vectors = vectorsRoot.get(vectorsRoot.size() - 1);

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
