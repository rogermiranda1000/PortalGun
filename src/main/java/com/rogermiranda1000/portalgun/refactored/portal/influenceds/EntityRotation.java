package com.rogermiranda1000.portalgun.refactored.portal.influenceds;

import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluenced;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluencer;
import org.bukkit.entity.Entity;

/**
 * An entity view is modified by portals
 */
class EntityRotation implements TrajectoryInfluenced {
    private final Entity e;

    public EntityRotation(Entity e) {
        this.e = e;
    }

    @Override
    public boolean validateInfluence(TrajectoryInfluencer influencer) {
        // TODO we must fake the position
        return false;
    }
}
