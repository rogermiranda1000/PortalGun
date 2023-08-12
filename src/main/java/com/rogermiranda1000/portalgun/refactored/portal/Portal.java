package com.rogermiranda1000.portalgun.refactored.portal;

import com.rogermiranda1000.portalgun.refactored.geometry.DelimitedPlane;
import com.rogermiranda1000.portalgun.refactored.geometry.Line;

public class Portal extends DelimitedPlane implements EntityTrajectoryInfluencer {
    /**
     * Portal-like portal
     * @param margin World position: x,y,z,world_msb,world_lsb
     * @param normal Out vector
     */
    public Portal(double[] margin, double[] normal) {
        super(margin, normal, new double[]{(long)1,(long)2,(long)0} /* Portal shape */);
    }

    @Override
    public Line modifyTrajectory(Line trajectory) {
        return trajectory;
    }
}
