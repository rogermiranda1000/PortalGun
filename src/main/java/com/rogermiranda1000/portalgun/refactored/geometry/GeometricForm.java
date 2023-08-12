package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

public abstract class GeometricForm <T extends Number> {
    @Nullable
    public abstract <O extends Number> Vector<Double> getIntersectionPoint(Line<O> line);
}
