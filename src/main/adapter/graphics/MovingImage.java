package main.adapter.graphics;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MovingImage {

    private final Image img;
    private final double velocity;
    private final double width;
    private final double height;
    private final double y;

    private boolean isMovingLeft;
    private double x;

    public MovingImage(Image img, Rectangle2D rectangle, float velocity, boolean isMovingLeft) {
        this.img = img;
        this.velocity = velocity;
        this.isMovingLeft = isMovingLeft;
        this.x = rectangle.getMinX();
        this.y = rectangle.getMinY();
        this.height = rectangle.getHeight();
        this.width = rectangle.getWidth();
    }

    public void move() {
        if (isMovingLeft) {
            x -= velocity;
        } else {
            x += velocity;
        }

        if (isMovingLeft && x + width < 0) {
            x = 880;
        } else if (x - width >= 840){
            x = -40;
        }
    }

    public void switchDirection() {
        isMovingLeft = !isMovingLeft;
    }

    public void draw(GraphicsContext graphics) {
        graphics.drawImage(img, x, y, width, height);
    }
}
