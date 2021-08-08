package main.io.graphics;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;

import static main.io.blackjack.ImageMap.*;

public class ImageReelAnimation extends AnimationTimer {

    private final GraphicsContext graphics;
    private final Collection<MovingImage> images = new ArrayList<>();

    public ImageReelAnimation(GraphicsContext graphics, boolean isMovingLeft) {
        this.graphics = graphics;

        final Image[] images = {symClubs(), symHearts(), symSpades(), symDiamonds()};

        for (int i = 0, x = 0, imageIndex = 0; i < 23; i++, x += 40) {
            final MovingImage img = new MovingImage(
                    images[imageIndex], 1, isMovingLeft, 40, 40, x, 0);

            this.images.add(img);

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
