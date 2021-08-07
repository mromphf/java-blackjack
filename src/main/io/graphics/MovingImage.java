package main.io.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MovingImage {

    private final Image img;
    private final float velocity;
    private final float width;
    private final float height;
    private final float y;

    private boolean isMovingLeft;
    private float x;

    public MovingImage(Image img, float velocity, boolean isMovingLeft, float height, float width, float x, float y) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.isMovingLeft = isMovingLeft;
        this.height = height;
        this.width = width;
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
