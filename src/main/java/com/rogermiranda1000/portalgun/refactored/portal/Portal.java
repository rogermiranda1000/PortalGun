package com.rogermiranda1000.portalgun.refactored.portal;

import com.rogermiranda1000.portalgun.refactored.geometry.DelimitedPlane;
import com.rogermiranda1000.portalgun.refactored.geometry.Line;

public class Portal extends DelimitedPlane<Long> implements EntityTrajectoryInfluencer {
    /**
     * Portal-like portal
     * @param margin World position: x,y,z,world_msb,world_lsb
     * @param normal Out vector
     */
    public Portal(Long[] margin, Double[] normal) {
        super(margin, normal, new Long[]{(long)1,(long)2,(long)0,(long)0,(long)0} /* Portal shape */);
    }

    @Override
    public Line<Double> modifyTrajectory(Line<Double> trajectory) {
        return trajectory;
    }
}
