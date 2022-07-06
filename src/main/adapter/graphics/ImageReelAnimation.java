package main.adapter.graphics;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.ui.Direction;

import java.util.Collection;
import java.util.LinkedList;

import static main.adapter.graphics.Vector.vector;

public class ImageReelAnimation extends AnimationTimer {

    private final static int CANVAS_WIDTH = 840;
    private final static int IMG_SIZE = 40;
    private final static int REEL_SPEED = 1;
    private final static int REEL_LENGTH = 23;
    private final static int ORIGIN = 0;

    private final GraphicsContext graphics;
    private final Collection<Moving<Image>> imageReel = new LinkedList<>();

    public ImageReelAnimation(Image[] images, GraphicsContext graphics, Direction direction) {
        this.graphics = graphics;

        for (int i = 0, position = 0, imageIndex = 0; i < REEL_LENGTH; i++, position += IMG_SIZE) {
            final Moving<Image> moving = new Moving<>(images[imageIndex], vector(position, IMG_SIZE), REEL_SPEED, direction);

            imageReel.add(moving);

            imageIndex++;

            if (imageIndex >= images.length) {
                imageIndex = 0;
            }
        }
    }

    @Override
    public void handle(long now) {
        clearCanvas();
        for (Moving<Image> img : imageReel) {
            img.move();
            graphics.drawImage(img.data(), img.vector().position, ORIGIN, img.vector().dimension, img.vector().dimension);
        }
    }

    public void switchDirection() {
        clearCanvas();
        for (Moving<Image> moving : imageReel) {
            moving.switchDirection();
        }
    }

    public void clearCanvas() {
        graphics.clearRect(ORIGIN, ORIGIN, CANVAS_WIDTH, IMG_SIZE);
    }
}
