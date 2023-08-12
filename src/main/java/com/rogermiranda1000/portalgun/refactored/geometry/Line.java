package com.rogermiranda1000.portalgun.refactored.geometry;

public class Line implements Cloneable {
    private final Vector point;
    private final Vector direction;

    public Line(double []point, double []direction) {
        this(new Vector(point), new Vector(direction));
    }

    public Line(Vector point, Vector direction) {
        if (point.getDimension() < direction.getDimension()) throw new IllegalArgumentException("Point dimension must be higher or equals than the direction");
        else if (point.getDimension() > direction.getDimension()) direction = direction.setDimension(point.getDimension()); // set the remaining as 0

        if (direction.isZero()) throw new IllegalArgumentException("Direction can't be zero");

        this.point = point.clone();
        this.direction = direction.clone().normalize();
    }

    public Vector getPoint() {
        return this.point.clone();
    }

    public Vector getDirection() {
        return this.direction.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Line)) return false;

        Line that = (Line) o;
        if (!this.point.equals(that.point)) return false; // this will also validate if they are same numeric type
        return this.direction.equals(that.direction);
    }

    @Override
    public Line clone() {
        return new Line(this.point, this.direction);
    }
}
