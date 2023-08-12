package com.rogermiranda1000.portalgun.refactored.portal;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;

public interface TrajectoryInfluencer {
    /**
     * Modify the
     * @param influenced Instance to modify its trajectory
     * @return true if the trajectory has been modified; false if not applicable
     */
    public boolean modifyTrajectory(TrajectoryInfluenced influenced);
}
