package main.adapter.graphics;

import static main.adapter.graphics.Direction.LEFT;

public class Vector {

    public double x;
    public final double y;

    private Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector vector(double x, double y) {
        return new Vector(x, y);
    }

    public void move(Direction direction, int velocity) {
        if (direction == LEFT) {
            x = (x - velocity);
        } else {
            x = (x + velocity);
        }
    }

    public void relocate(double position) {
        x = position;
    }
}
