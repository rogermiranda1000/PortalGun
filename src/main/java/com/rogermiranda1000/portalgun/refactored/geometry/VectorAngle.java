package com.rogermiranda1000.portalgun.refactored.geometry;

public class VectorAngle {
    private Vector angle;

    public VectorAngle(Vector v1, Vector v2) {
        int maxVectorDimension = Math.max(v1.getDimension(), v2.getDimension());
        int angleDimension = maxVectorDimension - 1;
        this.angle = new Vector(new double[angleDimension]);

        for (int n = 0; n < angleDimension; n++) {
            Plane projectingPlane = new Plane(new Vector(new double[maxVectorDimension]),
                                            new Vector(new double[maxVectorDimension]).setComponent(n, 1f));
            this.angle.setComponent(n, VectorAngle.get2dAngle(projectingPlane.project(v1), projectingPlane.project(v2)));
        }
    }

    private static double get2dAngle(Vector v1, Vector v2) {

    }

    public Vector applyAngle(Vector v) {
        return new Vector(); // TODO
    }
}
