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
    private boolean isInFlight = false;

    public TravelingImage(Image img, Rectangle2D rectangle, Point2D destination, float velocity) {
        this.destination = destination;
        this.img = img;
        this.velocity = velocity;
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
        this.x = x;
        this.y = y;
    }

    public void startMoving() {
        isInFlight = true;
    }

    public void stopMoving() {
        isInFlight = false;
    }

    public void move() {
        final Point2D loc = new Point2D(x, y);

        final double distance = loc.distance(destination);

        final Point2D nextLocation = nextLocation(loc, destination);

        x = nextLocation.getX();
        y = nextLocation.getY();
    }

    public Point2D nextLocation(Point2D p1, Point2D p2) {
        return new Point2D(
                p1.getX() + (p2.getX() - p1.getX()) * 0.01,
                p2.getY() + (p2.getY() - p1.getY()) * 0.01
        );
    };

    public Point2D makeMeAwesome(double x, double y) {
        return new Point2D(
                x + (this.x - x) / 2.0,
                y + (this.y - y) / 2.0);
    }

    public void draw(GraphicsContext graphics) {
        graphics.drawImage(img, x, y, width, height);
    }
}
