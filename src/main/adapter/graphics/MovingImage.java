package main.adapter.graphics;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.adapter.ui.Direction;

import static main.adapter.ui.Direction.LEFT;
import static main.adapter.ui.Direction.RIGHT;

public class MovingImage {

    private final Image img;
    private final double velocity;
    private final double width;
    private final double height;
    private final double y;

    private Direction direction;
    private double x;

    public MovingImage(Image img, Rectangle2D rectangle, Direction direction, float velocity ) {
        this.img = img;
        this.velocity = velocity;
        this.direction = direction;
        this.x = rectangle.getMinX();
        this.y = rectangle.getMinY();
        this.height = rectangle.getHeight();
        this.width = rectangle.getWidth();
    }

    public void move() {
        if (direction == LEFT) {
            x -= velocity;
        } else {
            x += velocity;
        }

        if ((direction == LEFT) && x + width < 0) {
            x = 880;
        } else if (x - width >= 840){
            x = -40;
        }
    }

    public void switchDirection() {
        direction = direction == LEFT ? RIGHT : LEFT;
    }

    public void draw(GraphicsContext graphics) {
        graphics.drawImage(img, x, y, width, height);
    }
}
