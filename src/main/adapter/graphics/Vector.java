package main.adapter.graphics;

public class Vector {

    public final int position;
    public final int dimension;

    private Vector(int position, int dimension) {
        this.position = position;
        this.dimension = dimension;
    }

    public static Vector vector(int position, int dimension) {
        return new Vector(position, dimension);
    }
}
