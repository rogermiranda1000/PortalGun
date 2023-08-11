package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Vector <T extends Number> {
    private final T []vector;
    @Nullable
    private final Class<? extends Number> vectorType;

    @SafeVarargs
    public Vector(T ...vector) {
        this.vector = vector;
        this.vectorType = (vector.length > 0 ? vector[0].getClass() : null);
    }

    public int getDimension() {
        return this.vector.length;
    }

    public T []getVector() {
        return this.vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;

        Vector<?> that = (Vector<?>) o;
        if (this.getDimension() != that.getDimension() || !Objects.equals(this.vectorType, that.vectorType)) return false;
        for (int n = 0; n < this.getDimension(); n++) {
            if (!this.vector[n].equals(that.vector[n])) return false; // not equals
        }
        return true; // all equals
    }
}
