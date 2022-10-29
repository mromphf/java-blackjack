package com.blackjack.main.adapter.graphics;

import static com.blackjack.main.adapter.graphics.Direction.LEFT;
import static com.blackjack.main.adapter.graphics.Direction.RIGHT;

public class Moving<T> {

    private final static int RIGHT_BOUNDARY = 880;

    private final T data;
    private final int velocity;
    private final Vector vector;

    private Direction direction;

    public Moving(T data,
                  final Vector vector,
                  final int velocity,
                  Direction direction) {
        this.data = data;
        this.velocity = velocity;
        this.vector = vector;
        this.direction = direction;
    }

    public void move() {
        vector.move(direction, velocity);

        /*
            This procedure is aware of its surroundings, which means the movement of the object is complected
            with the space it's moving through. We could solve this by pushing the boundary evaluations into
            a subclass.
         */

        if ((direction == LEFT) && ((vector.x + vector.y) < 0)) {
            vector.relocate(RIGHT_BOUNDARY);
        } else if (vector.x >= RIGHT_BOUNDARY) {
            vector.relocate((vector.y * (-1)));
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
