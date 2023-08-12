package com.rogermiranda1000.portalgun.refactored.portal.influenceds;

import com.rogermiranda1000.portalgun.refactored.geometry.Line;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluenced;
import org.bukkit.entity.Entity;

/**
 * An entity position and velocity is modified by portals
 */
public class EntityPosition implements TrajectoryInfluenced {
    private final Entity e;

    public EntityPosition(Entity e) {
        this.e = e;
    }

    @Override
    public void setTrajectory(Line<Double> t) {

    }

    @Override
    public Line<Double> getCurrentTrajectory() {
        return null;
    }
}
