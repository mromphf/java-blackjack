package main.adapter.graphics.animations;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.graphics.Moving;

import java.util.Collection;

public class ImageReel extends AnimationTimer {

    private final static int IMG_SIZE = 40;
    private final static int CANVAS_WIDTH = 840;
    private final static int ORIGIN = 0;

    private final GraphicsContext graphics;
    private final Collection<Moving<Image>> imageReel;

    public ImageReel(Collection<Moving<Image>> imageReel, GraphicsContext graphics) {
        this.imageReel = imageReel;
        this.graphics = graphics;
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(ORIGIN, ORIGIN, CANVAS_WIDTH, IMG_SIZE);

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
        imageReel.forEach(Moving::switchDirection);
    }
}
