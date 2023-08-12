package com.rogermiranda1000.portalgun.refactored.portal;

public interface TrajectoryInfluenced {
    /**
     * Tries to update its trajectory with `influencer`
     * @param influencer Influencer that can modify the instance trajectory
     * @return If the trajectory was changed (true), or not (false)
     */
    public boolean validateInfluence(TrajectoryInfluencer influencer);
}
