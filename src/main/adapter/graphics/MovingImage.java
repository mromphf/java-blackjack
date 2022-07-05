package main.adapter.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.ui.Direction;

import static main.adapter.ui.Direction.LEFT;
import static main.adapter.ui.Direction.RIGHT;

public class MovingImage {

    private final static int TOP = 0;
    private final static int BOUNDARY_LEFTMOST = 880;

    private final Image img;
    private final int velocity;
    private final Vector vector;

    private Direction direction;

    public MovingImage(final Image img,
                       final Vector v,
                       final int velocity,
                       Direction direction) {
        this.img = img;
        this.velocity = velocity;
        this.vector = v;
        this.direction = direction;
    }

    public void move() {
        vector.move(direction, velocity);

        if ((direction == LEFT) && ((vector.position + vector.dimension) < 0)) {
            vector.relocate(BOUNDARY_LEFTMOST);
        } else if ((vector.position - vector.dimension) >= (BOUNDARY_LEFTMOST - vector.dimension)) {
            vector.relocate((vector.dimension * (-1)));
        }
    }

    public void switchDirection() {
        direction = direction == LEFT ? RIGHT : LEFT;
    }

    public void draw(GraphicsContext graphics) {
        graphics.drawImage(img, vector.position, TOP, vector.dimension, vector.dimension);
    }
}
