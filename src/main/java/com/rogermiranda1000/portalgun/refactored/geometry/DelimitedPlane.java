package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

public class DelimitedPlane <T extends Number> extends Plane<T> {
    private final T[] dimensions;

    /**
     * Non-infinite plane
     * @param margin One of the 4 margins of the plane
     * @param normal Rotation of the plane
     * @param dimensions Components added to `margin` to form the delimited plane
     */
    public DelimitedPlane(T[] margin, Double[] normal, T[] dimensions) {
        super(margin, normal);

        if (margin.length != dimensions.length) throw new IllegalArgumentException("The margin and the dimensions of the plane must be the same");
        this.dimensions = dimensions; // TODO we can't calculate "without rotating normal" because it makes no mathematical sense
    }

    @Override
    @Nullable
    public <O extends Number> Vector<Double> getIntersectionPoint(Line<O> line) {

    }
}
