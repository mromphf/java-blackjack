package main.adapter.graphics;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.ui.Direction;

public class ImageReelAnimation extends AnimationTimer {

    private final GraphicsContext graphics;
    private final MovingImage[] imageReel = new MovingImage[23];

    public ImageReelAnimation(Image[] images, GraphicsContext graphics, Direction direction) {
        this.graphics = graphics;

        for (int i = 0, x = 0, imageIndex = 0; i < 23; i++, x += 40) {
            final Rectangle2D rectangle = new Rectangle2D(x, 0, 40, 40);

            imageReel[i] = new MovingImage(images[imageIndex], rectangle, direction,1);

            imageIndex++;

            if (imageIndex >= images.length) {
                imageIndex = 0;
            }
        }
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(0, 0, 840, 40);
        for (MovingImage movingImage : imageReel) {
            movingImage.move();
            movingImage.draw(graphics);
        }
    }

    public void switchDirection() {
        graphics.clearRect(0, 0, 840, 40);
        for (MovingImage movingImage : imageReel) {
            movingImage.switchDirection();
        }
    }
}
