package com.rogermiranda1000.portalgun.refactored.geometry;

public class Line <T extends Number> {
    private final Vector<T> point;
    private final Vector<Double> direction;


    public Line(T []point, Double []direction) {
        if (point.length != direction.length) throw new IllegalArgumentException("Point and direction dimensions must be the same");

        this.point = new Vector<>(point);
        this.direction = new Vector<>(direction);
    }

    public Vector<T> getPoint() {
        return this.point;
    }

    public Vector<Double> getDirection() {
        return this.direction;
    }
}
