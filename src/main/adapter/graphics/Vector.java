package main.adapter.graphics;

import static main.adapter.graphics.Direction.LEFT;

public class Vector {

    public final int dimension;
    public int position;

    private Vector(int position, int dimension) {
        this.position = position;
        this.dimension = dimension;
    }

    public static Vector vector(int position, int dimension) {
        return new Vector(position, dimension);
    }

    public void move(Direction direction, int velocity) {
        if (direction == LEFT) {
            position = (position - velocity);
        } else {
            position = (position + velocity);
        }
    }

    public void relocate(int position) {
        this.position = position;
    }
}
