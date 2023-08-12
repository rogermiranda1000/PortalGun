package com.rogermiranda1000.portalgun.refactored.portal;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;

public interface TrajectoryInfluencer {
    /**
     * Modifies a trajectory, if it's inside its influence area
     * @param trajectory Old trajectory
     * @return New trajectory (if it's the same )
     */
    public Line modifyTrajectory(Line trajectory);
}
