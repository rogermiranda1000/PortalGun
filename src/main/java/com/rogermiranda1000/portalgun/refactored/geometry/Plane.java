package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Plane extends GeometricForm {
    private final Vector point;
    private final Vector normal;
    private final double d;

    public Plane(double []point, double []normal) {
        if (point.length < normal.length) throw new IllegalArgumentException("Point dimension must be higher or equals than the normal");
        else if (point.length > normal.length) normal = Arrays.copyOf(normal, point.length); // set the remaining as 0

        this.point = new Vector(point);
        this.normal = new Vector(normal).normalize();
        this.d = this.normal.dot(this.point); // @ref https://mathinsight.org/distance_point_plane#distance_formula_1
    }

    public Vector getPoint() {
        return this.point;
    }

    public Vector getNormal() {
        return this.normal;
    }

    /**
     * Check if a line intersects with the plane.
     * @author Intersecting Segment Against Plane, on Real-Time Collision Detection
     * @param line Line to check the intersection with the plane
     * @return Point where the line intersects (null if none)
     */
    @Override
    @Nullable
    public Vector getIntersectionPoint(Line line) {
        // Compute the t value for the directed line ab intersecting the plane
        Vector ab = line.getDirection();
        double t = (this.d - this.normal.dot(line.getPoint())) / this.normal.dot(ab);

        if (t < 0.f) return null; // no intersection

        // the intersection is forward; compute and return the intersection point
        return line.getPoint().add(ab.multiply(t));
    }
}
