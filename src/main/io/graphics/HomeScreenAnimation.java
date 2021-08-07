package main.io.graphics;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;

import static main.io.blackjack.ImageMap.*;

public class HomeScreenAnimation extends AnimationTimer {

    private final GraphicsContext graphics;
    private final Collection<MovingImage> topImages = new ArrayList<>();

    public HomeScreenAnimation(GraphicsContext graphics) {
        this.graphics = graphics;
        final Image[] images = {symClubs(), symHearts(), symSpades(), symDiamonds()};

        for (int i = 0, x = 0, imageIndex = 0; i < 19; i++, x += 50) {
            topImages.add(new MovingImage(images[imageIndex], 1, true, 50, 50, x, 0));

            imageIndex++;

            if (imageIndex >= 4) {
                imageIndex = 0;
            }
        }
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(0, 0, 850, 50);
        topImages.forEach(MovingImage::move);
        topImages.forEach(img -> img.draw(graphics));
    }
}
