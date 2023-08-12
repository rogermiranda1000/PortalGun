package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class DelimitedPlane extends Plane {
    private final double[] dimensions;

    /**
     * Non-infinite plane
     * @param margin One of the 4 margins of the plane
     * @param normal Rotation of the plane
     * @param dimensions Components added to `margin` after applying the normal to form the delimited plane
     */
    public DelimitedPlane(double[] margin, double[] normal, double[] dimensions) {
        super(margin, normal);

        if (margin.length < dimensions.length) throw new IllegalArgumentException("Point dimension must be higher or equals than the dimensions");
        else if (margin.length > dimensions.length) dimensions = Arrays.copyOf(dimensions, margin.length); // set the remaining as 0
        this.dimensions = dimensions;
    }

    @Override
    @Nullable
    public Vector getIntersectionPoint(Line line) {

    }
}
