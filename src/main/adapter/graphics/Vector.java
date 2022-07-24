package main.adapter.graphics;

import static main.adapter.graphics.Direction.LEFT;

public class Vector {

    public int x;
    public final int y;

    private Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector vector(int x, int y) {
        return new Vector(x, y);
    }

    public void move(Direction direction, int velocity) {
        if (direction == LEFT) {
            x = (x - velocity);
        } else {
            x = (x + velocity);
        }
    }

    public void relocate(int position) {
        this.x = position;
    }
}
