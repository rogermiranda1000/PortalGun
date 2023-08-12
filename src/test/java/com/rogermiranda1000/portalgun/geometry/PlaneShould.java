package com.rogermiranda1000.portalgun.geometry;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Plane;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlaneShould {
    @Test
    public void returnCollisionPoint() {
        // a horizontal x-z plane
        Plane p = new Plane(
                new double[]{0,0,0},
                new double[]{0,1,0}
        );
        Line incidence = new Line(
                new double[]{1,1,0},
                new double[]{0,-1,0}
        );

        Vector intersectionAt = p.getIntersectionPoint(incidence);

        assertEquals(new Vector(1,0,0), intersectionAt);
    }

    @Test
    public void returnNothingIfParallel() {
        // a horizontal x-z plane
        Plane p = new Plane(
                new double[]{0,0,0},
                new double[]{0,1,0}
        );
        Line parallel = new Line(
                new double[]{1,1,0},
                new double[]{1,0,0}
        );

        Vector intersectionAt = p.getIntersectionPoint(parallel);

        assertEquals(null, intersectionAt);
    }
}
