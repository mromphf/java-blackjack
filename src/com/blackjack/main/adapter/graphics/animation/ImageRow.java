package com.blackjack.main.adapter.graphics.animation;

import com.blackjack.main.adapter.graphics.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;
import java.util.SortedMap;

public class ImageRow {

    private final SortedMap<Integer, Vector> vectors;
    private final GraphicsContext graphics;
    private final List<Image> images;

    public static ImageRow imageRow(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
            List<Image> images) {
        return new ImageRow(graphics, vectors, images);
    }

    private ImageRow(
            GraphicsContext graphics,
            SortedMap<Integer, Vector> vectors,
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
