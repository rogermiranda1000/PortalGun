package com.rogermiranda1000.portalgun.geometry;

import com.rogermiranda1000.portalgun.refactored.geometry.Vector;
import com.rogermiranda1000.portalgun.refactored.geometry.VectorAngle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorAngleShould {
    @Test
    public void rotate2dAngle() {
        // go down 90º
        Vector v1 = new Vector(1, 0),
                v2 = new Vector(0, -1);
        Vector applyTo = new Vector(-1, 0),
                expected = new Vector(0, 1);

        VectorAngle angle = new VectorAngle(v1, v2);
        assertEquals(expected, angle.applyAngle(applyTo));
    }

    @Test
    public void rotate3dAngle() {
        // go down 90º, 45º right
        Vector v1 = new Vector(1, 0, 0),
                v2 = new Vector(0, -1, -1);
        Vector applyTo = new Vector(0, 1, 0),
                expected = new Vector(1, 0, 1).normalize();

        VectorAngle angle = new VectorAngle(v1, v2);
        assertEquals(expected, angle.applyAngle(applyTo).normalize());
    }
}
