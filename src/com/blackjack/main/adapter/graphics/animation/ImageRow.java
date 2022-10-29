package com.blackjack.main.adapter.graphics.animation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.blackjack.main.adapter.graphics.Vector;

import java.util.List;

public class ImageRow {

    private final List<Vector> vectors;
    private final GraphicsContext graphics;
    private final List<Image> images;

    public static ImageRow imageRow(
            GraphicsContext graphics,
            List<Vector> vectors,
            List<Image> images) {
        return new ImageRow(graphics, vectors, images);
    }

    private ImageRow(
            GraphicsContext graphics,
            List<Vector> vectors,
            List<Image> images) {

        this.graphics = graphics;
        this.vectors = vectors;
        this.images = images;
    }

    public void draw() {
        for (int i = 0; i < images.size(); i++) {
            final Image img = images.get(i);
            final Vector vec = vectors.get(i);

            graphics.drawImage(img, vec.x, vec.y);
        }
    }
}
