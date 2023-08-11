package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

public class Plane <T extends Number> {
    private final Vector<T> point;
    private final Vector<Double> normal;

    public Plane(T []point, Double []normal) {
        if (point.length != normal.length) throw new IllegalArgumentException("Point and normal dimensions must be the same");

        this.point = new Vector<>(point);
        this.normal = new Vector<>(normal);
    }

    public Vector<T> getPoint() {
        return this.point;
    }

    public Vector<Double> getNormal() {
        return this.normal;
    }

    /**
     * Check if a line intersects with the plane
     * @param line Line to check the intersection with the plane
     * @return Point where the line intersects (null if none)
     * @param <O> Point numeric precision type
     */
    @Nullable
    public <O extends Number> Vector<Double> getIntersectionPoint(Line<O> line) {

    }
}
