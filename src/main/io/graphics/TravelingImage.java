package main.io.graphics;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TravelingImage {

    private final Image img;
    private final Point2D destination;
    private final float velocity;
    private final double width;
    private final double height;

    private double y;
    private double x;

    public TravelingImage(Image img, Rectangle2D rectangle, Point2D destination, float velocity) {
        this.destination = destination;
        this.img = img;
        this.velocity = velocity;
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
        this.x = rectangle.getMinX();
        this.y = rectangle.getMaxY();
    }

    public void move() {
        if (!hasReachedDestination()) {
            final Point2D loc = new Point2D(x, y);
            final Point2D nextLocation = nextLocation(loc, destination);

            x = nextLocation.getX();
            y = nextLocation.getY();
        }
    }

    public boolean hasReachedDestination() {
        return x == destination.getX() &&
               y == destination.getY();
    }

    public Point2D nextLocation(Point2D src, Point2D dest) {
        if (src.distance(dest) < 0.1)
            return dest;

        return new Point2D(
                src.getX() + ((dest.getX() - src.getX()) * (velocity * 0.1)),
                src.getY() + ((dest.getY() - src.getY()) * (velocity * 0.1))
        );
    }

    public void draw(GraphicsContext graphics) {
        graphics.drawImage(img, x, y, width, height);
    }
}
