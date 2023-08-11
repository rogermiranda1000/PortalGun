package com.rogermiranda1000.portalgun.refactored.portals;

import com.rogermiranda1000.portalgun.refactored.geometry.DelimitedPlane;
import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.geometry.Vector;

public class Portal extends DelimitedPlane<Integer> implements TrajectoryInfluencer<Integer> {
    /**
     * Portal-like portal
     * @param margin World position: world_msb,world_lsb,x,y,z
     * @param normal Out vector
     */
    public Portal(Integer[] margin, Double[] normal) {
        super(margin, normal, new Integer[]{0,0,1,2,0} /* Portal shape */);
    }

    /**
     * Modifies the trajectory of an incoming object
     * @param in Incidence vector
     * @return New position and trajectory of the incoming object
     */
    @Override
    public Line<Integer> getNewTrajectory(Vector<Double> in) {

    }
}
