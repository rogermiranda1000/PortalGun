package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

public abstract class GeometricForm {
    @Nullable
    public abstract Vector getIntersectionPoint(Line line);
}
