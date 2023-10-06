package com.rogermiranda1000.portalgun.refactored.portal.influenceds;

import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluenced;
import com.rogermiranda1000.portalgun.refactored.portal.TrajectoryInfluencer;
import org.bukkit.entity.Entity;

public class EntityInfluenced implements TrajectoryInfluenced {
    private final TrajectoryInfluenced positionInfluenced, rotationInfluenced;

    public EntityInfluenced(Entity e) {
        this.positionInfluenced = new EntityPosition(e);
        this.rotationInfluenced = new EntityRotation(e);
    }
    @Override
    public boolean validateInfluence(TrajectoryInfluencer influencer) {
        if (!(influencer instanceof EntityTrajectoryInfluencer)) return false;

        boolean changed = this.positionInfluenced.validateInfluence(influencer);
        if (!changed) return false;

        this.rotationInfluenced.validateInfluence(influencer);
        return true;
    }
}
