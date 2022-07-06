package main.adapter.graphics;

import main.adapter.ui.Direction;

import static main.adapter.ui.Direction.LEFT;
import static main.adapter.ui.Direction.RIGHT;

public class Moving<T> {

    private final static int RIGHT_BOUNDARY = 880;

    private final T data;
    private final int velocity;
    private final Vector vector;

    private Direction direction;

    public Moving(T data,
                  final Vector v,
                  final int velocity,
                  Direction direction) {
        this.data = data;
        this.velocity = velocity;
        this.vector = v;
        this.direction = direction;
    }

    public void move() {
        vector.move(direction, velocity);

        if ((direction == LEFT) && ((vector.position + vector.dimension) < 0)) {
            vector.relocate(RIGHT_BOUNDARY);
        } else if (vector.position >= RIGHT_BOUNDARY) {
            vector.relocate((vector.dimension * (-1)));
        }
    }

    public void switchDirection() {
        direction = direction == LEFT ? RIGHT : LEFT;
    }

    public T data() {
        return data;
    }

    public Vector vector() {
        return vector;
    }
}
