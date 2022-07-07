package main.adapter.graphics;

import com.google.inject.Inject;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Collection;

public class ImageReelAnimation extends AnimationTimer {

    private final static int IMG_SIZE = 40;
    private final static int CANVAS_WIDTH = 840;
    private final static int ORIGIN = 0;

    private final GraphicsContext graphics;
    private final Collection<Moving<Image>> imageReel;

    @Inject
    public ImageReelAnimation(Collection<Moving<Image>> imageReel, GraphicsContext graphics) {
        this.imageReel = imageReel;
        this.graphics = graphics;
    }

    @Override
    public void handle(long now) {
        clearCanvas();
        for (Moving<Image> img : imageReel) {
            img.move();

            final int x = img.vector().position;
            final int y = 0;
            final int w = img.vector().dimension;
            final int h = img.vector().dimension;

            graphics.drawImage(img.data(), x, y, w, h);
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
