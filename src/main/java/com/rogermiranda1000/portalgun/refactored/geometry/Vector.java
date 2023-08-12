package com.rogermiranda1000.portalgun.refactored.geometry;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Vector <T extends Number> {
    private final T []vector;
    @Nullable
    private final Class<T> vectorType;

    @SafeVarargs
    @SuppressWarnings("ConstantConditions")
    public Vector(T ...vector) throws IllegalArgumentException {
        this.vectorType = Vector.getType(vector);

        // set the vector (0 if null)
        this.vector = vector;
        for (int n = 0; n < this.vector.length; n++) {
            // vectorType won't be null, because to be null `this.vector.length` needs to be 0 (so it won't enter the loop)
            if (this.vector[n] == null) this.vector[n] = this.vectorType.cast(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <O extends Number> Class<O> getType(O []vector) throws IllegalArgumentException {
        if (vector.length == 0) return null; // no elements
        for (O e : vector) {
            if (e != null) return (Class<O>) e.getClass();
        }

        // all elements are null
        throw new IllegalArgumentException("At least one element needs to be !=null");
    }

    public int getDimension() {
        return this.vector.length;
    }

    public T []getVector() {
        return this.vector;
    }

    public double length() throws ArithmeticException {
        return Math.sqrt(this.lengthSquared());
    }

    @SuppressWarnings("ConstantConditions")
    public double lengthSquared() throws ArithmeticException {
        if (this.getDimension() == 0) throw new ArithmeticException("Trying to get length of zero-dimension vector");
        double sum = 0;
        for (T e : this.vector) sum += e.doubleValue()*e.doubleValue();
        return sum;
    }

    public boolean isNormalized() throws ArithmeticException {
        return Math.abs(this.lengthSquared() - 1.0) < getEpsilon();
    }

    public Vector<? extends Number> normalize(boolean modifyTypeIfNeeded) throws ArithmeticException {
        if (this.isNormalized()) return this; // Vector<T>
        if (!modifyTypeIfNeeded && !Double.class.equals(this.vectorType)) throw new ArithmeticException("Unable to get a normalized vector");

        double length = this.length();
        Double []parameters = new Double[this.getDimension()];
        for (int n = 0; n < this.getDimension(); n++) {
            parameters[n] = new Double(this.vector[n].doubleValue() / length);
        }
        return new Vector<>(parameters); // Vector<Double>
    }

    @SuppressWarnings("unchecked")
    public Vector<T> normalize() throws ArithmeticException {
        return (Vector<T>) this.normalize(false);
    }

    public T getComponent(int index) throws IndexOutOfBoundsException {
        if (this.getDimension() <= index) throw new IndexOutOfBoundsException("Can't access index " + index + " for a " + this.getDimension() + " dimensions vector!");
        return this.vector[index];
    }

    public T x() throws IndexOutOfBoundsException {
        return this.getComponent(0);
    }

    public T y() throws IndexOutOfBoundsException {
        return this.getComponent(1);
    }

    public T z() throws IndexOutOfBoundsException {
        return this.getComponent(2);
    }

    public static double getEpsilon() {
        return 1.0E-6;
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
