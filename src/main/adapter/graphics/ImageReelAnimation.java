package main.adapter.graphics;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;

import static main.adapter.blackjack.ImageMap.*;

public class ImageReelAnimation extends AnimationTimer {

    private final GraphicsContext graphics;
    private final Collection<MovingImage> images = new ArrayList<>();

    public ImageReelAnimation(GraphicsContext graphics, boolean isMovingLeft) {
        this.graphics = graphics;

        final Image[] images = {symClubs(), symHearts(), symSpades(), symDiamonds()};

        // Assemble the image reel
        for (int i = 0, x = 0, imageIndex = 0; i < 23; i++, x += 40) {
            final Rectangle2D rectangle = new Rectangle2D(x, 0, 40, 40);

            this.images.add(new MovingImage(images[imageIndex], rectangle, 1, isMovingLeft));

            imageIndex++;

            if (imageIndex >= images.length) {
                imageIndex = 0;
            }
        }
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(0, 0, 840, 40);
        images.forEach(MovingImage::move);
        images.forEach(img -> img.draw(graphics));
    }

    public void switchDirection() {
        images.forEach(MovingImage::switchDirection);
    }
}
