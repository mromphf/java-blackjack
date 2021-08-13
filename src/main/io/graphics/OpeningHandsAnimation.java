package main.io.graphics;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;

public class OpeningHandsAnimation extends AnimationTimer {

    private final AnimationCallback callback;
    private final GraphicsContext graphics;
    private final TravelingImage img;
    private final Collection<TravelingImage> images;

    public OpeningHandsAnimation(GraphicsContext graphics, TravelingImage img, AnimationCallback callback, Collection<TravelingImage> images) {
        this.graphics = graphics;
        this.callback = callback;
        this.img = img;
        this.images = images;
        images.add(img);
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(0, 0, 2000, 2000);
        images.forEach(TravelingImage::move);
        images.forEach(img -> img.draw(graphics));

        if (img.hasReachedDestination()) {
            stop();

            if (callback != null) {
                callback.run(images);
            }
        }
    }
}
