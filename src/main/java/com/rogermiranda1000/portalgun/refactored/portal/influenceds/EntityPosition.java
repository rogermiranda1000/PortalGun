package com.rogermiranda1000.portalgun.refactored.portal.influenceds;

import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluenced;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluencer;
import org.bukkit.entity.Entity;

/**
 * An entity position and velocity is modified by portals
 */
class EntityPosition implements TrajectoryInfluenced {
    private final Entity e;

    public EntityPosition(Entity e) {
        this.e = e;
    }

    @Override
    public boolean validateInfluence(TrajectoryInfluencer influencer) {
        return false;
    }
}
