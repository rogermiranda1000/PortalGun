package com.rogermiranda1000.portalgun.refactored.portals;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;

public interface TrajectoryInfluencer<T extends Number> {
    public Line<T> getNewTrajectory(Vector<Double> in);
}
