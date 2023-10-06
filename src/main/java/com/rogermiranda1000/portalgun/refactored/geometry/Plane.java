package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Plane extends GeometricForm {
    private final Vector point;
    private final Vector normal;
    private final double d;

    public Plane(double []point, double []normal) {
        this(new Vector(point), new Vector(normal));
    }

    public Plane(Vector point, Vector normal) {
        if (point.getDimension() < normal.getDimension()) throw new IllegalArgumentException("Point dimension must be higher or equals than the normal");
        else if (point.getDimension() > normal.getDimension()) normal.setDimension(point.getDimension()); // set the remaining as 0

        if (normal.isZero()) throw new IllegalArgumentException("Normal can't be zero");

        this.point = point.clone();
        this.normal = normal.clone().normalize();
        this.d = this.normal.dot(this.point); // @ref https://mathinsight.org/distance_point_plane#distance_formula_1
    }

    public Vector getPoint() {
        return this.point.clone();
    }

    public Vector getNormal() {
        return this.normal.clone();
    }

    /**
     * Projects vector `v` into this plane
     * @param v Vector to project
     * @return Projected vector
     */
    public Vector project(Vector v) {
        // TODO
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
        double t = (this.d - this.getNormal().dot(line.getPoint())) / this.normal.dot(ab);

        if (t < 0.f) return null; // no intersection

        // the intersection is forward; compute and return the intersection point
        return line.getPoint().add(ab.multiply(t));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Plane)) return false;

        Plane that = (Plane) o;
        if (!this.point.equals(that.point)) return false; // this will also validate if they are same numeric type
        return this.normal.equals(that.normal);
    }
}
