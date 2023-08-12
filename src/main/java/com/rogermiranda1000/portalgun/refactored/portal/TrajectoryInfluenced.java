package com.rogermiranda1000.portalgun.refactored.portal;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;

public interface TrajectoryInfluenced {
    public void setTrajectory(Line<Double> t);
    public Line<Double> getCurrentTrajectory();
}
