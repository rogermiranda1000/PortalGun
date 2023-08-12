package com.rogermiranda1000.portalgun.refactored.geometry;

import java.util.Arrays;

public class Line {
    private final Vector point;
    private final Vector direction;


    public Line(double []point, double []direction) {
        if (point.length < direction.length) throw new IllegalArgumentException("Point dimension must be higher or equals than the direction");
        else if (point.length > direction.length) direction = Arrays.copyOf(direction, point.length); // set the remaining as 0

        this.point = new Vector(point);
        this.direction = new Vector(direction);
    }

    public Vector getPoint() {
        return this.point;
    }

    public Vector getDirection() {
        return this.direction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Line)) return false;

        Line that = (Line) o;
        if (!this.point.equals(that.point)) return false; // this will also validate if they are same numeric type
        return this.direction.equals(that.direction);
    }
}
