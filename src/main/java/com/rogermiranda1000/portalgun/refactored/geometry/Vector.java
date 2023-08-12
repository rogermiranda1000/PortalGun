package com.rogermiranda1000.portalgun.refactored.geometry;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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

    public double getComponent(int index) throws IndexOutOfBoundsException {
        if (this.getDimension() <= index) throw new IndexOutOfBoundsException("Can't access index " + index + " for a " + this.getDimension() + " dimensions vector!");
        return this.vector[index];
    }

    public Vector setComponent(int index, double value) throws IndexOutOfBoundsException {
        if (this.getDimension() <= index) throw new IndexOutOfBoundsException("Can't access index " + index + " for a " + this.getDimension() + " dimensions vector!");
        this.vector[index] = value;
        return this;
    }

    public double x() throws IndexOutOfBoundsException {
        return this.getComponent(0);
    }

    public double y() throws IndexOutOfBoundsException {
        return this.getComponent(1);
    }

    public double z() throws IndexOutOfBoundsException {
        return this.getComponent(2);
    }

    public static double getEpsilon() {
        return 1.0E-6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;

        Vector that = (Vector) o;
        if (this.getDimension() != that.getDimension()) return false;
        for (int n = 0; n < this.getDimension(); n++) {
            if (this.vector[n] != that.vector[n]) return false; // not equals
        }
        return true; // all equals
    }

    @Override
    public Vector clone() {
        return new Vector(this.vector);
    }
}
