package com.rogermiranda1000.portalgun.refactored.geometry;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An Rn vector
 */
public class Vector implements Cloneable {
    private double []vector;

    public Vector(double ...vector) throws IllegalArgumentException {
        this.vector = vector.clone();
    }

    public int getDimension() {
        return this.vector.length;
    }

    public Vector setDimension(int size) {
        this.vector = Arrays.copyOf(this.vector, size);
        return this;
    }

    public double length() throws ArithmeticException {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        double sum = 0;
        for (double e : this.vector) sum += e*e;
        return sum;
    }

    public boolean isZero() {
        // TODO double precision?
        return this.lengthSquared() == 0.f;
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1.0) < getEpsilon();
    }

    public Vector normalize() {
        if (this.isNormalized()) return this;

        double length = this.length();
        return this.divide(length);
    }

    public double dot(@NotNull Vector v) {
        if (this.getDimension() != v.getDimension()) throw new IllegalArgumentException("Dimensions must be equals");

        double dot = 0;
        for (int n = 0; n < this.getDimension(); n++) dot += this.getComponent(n)*v.getComponent(n);
        return dot;
    }

    public Vector add(Vector v) {
        if (this.getDimension() != v.getDimension()) throw new IllegalArgumentException("Dimensions must be equals");

        for (int n = 0; n < this.getDimension(); n++) this.setComponent(n, this.getComponent(n) * v.getComponent(n));
        return this;
    }

    public Vector multiply(double amount) {
        for (int n = 0; n < this.getDimension(); n++) this.setComponent(n, this.getComponent(n) * amount);
        return this;
    }

    public Vector divide(double amount) {
        for (int n = 0; n < this.getDimension(); n++) this.setComponent(n, this.getComponent(n) / amount);
        return this;
    }

    public double getComponent(int index) {
        if (this.getDimension() <= index) return 0;
        return this.vector[index];
    }

    public Vector setComponent(int index, double value) throws IndexOutOfBoundsException {
        if (this.getDimension() <= index) throw new IndexOutOfBoundsException("Can't access index " + index + " for a " + this.getDimension() + " dimensions vector!");
        this.vector[index] = value;
        return this;
    }

    public double x() {
        return this.getComponent(0);
    }

    public double y() {
        return this.getComponent(1);
    }

    public double z() {
        return this.getComponent(2);
    }

    public static double getEpsilon() {
        return 1.0E-6;
    }

    @Override
    public String toString() {
        return '(' + Arrays.stream(this.vector).mapToObj(String::valueOf).collect(Collectors.joining(",")) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;

        Vector that = (Vector) o;
        if (this.getDimension() != that.getDimension()) return false;
        for (int n = 0; n < this.getDimension(); n++) {
            if (Math.abs(this.vector[n] - that.vector[n]) > Vector.getEpsilon()) return false; // not equals
        }
        return true; // all equals
    }

    @Override
    public Vector clone() {
        return new Vector(this.vector);
    }
}
