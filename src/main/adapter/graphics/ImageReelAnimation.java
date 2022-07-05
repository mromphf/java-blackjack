package main.adapter.graphics;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.ui.Direction;

import static main.adapter.graphics.Vector.vector;

public class ImageReelAnimation extends AnimationTimer {

    private final static int IMG_SIZE = 40;
    private final static int REEL_SPEED = 1;
    private final static int REEL_SIZE = 23;

    private final GraphicsContext graphics;
    private final MovingImage[] imageReel = new MovingImage[REEL_SIZE];

    public ImageReelAnimation(Image[] images, GraphicsContext graphics, Direction direction) {
        this.graphics = graphics;

        for (int i = 0, position = 0, imageIndex = 0; i < 23; i++, position += IMG_SIZE) {

            imageReel[i] = new MovingImage(
                    images[imageIndex],
                    vector(position, IMG_SIZE),
                    REEL_SPEED, direction);

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
            graphics.drawImage(movingImage.img(), movingImage.vector().position, 0, movingImage.vector().dimension, movingImage.vector().dimension);
        }
    }

    public void switchDirection() {
        graphics.clearRect(0, 0, 840, 40);
        for (MovingImage movingImage : imageReel) {
            movingImage.switchDirection();
        }
    }
}
