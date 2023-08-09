package com.rogermiranda1000.portalgun.blocks.beam;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public interface BeamDisruptedEvent {
    public void onBeamDisrupted(Entity e, Location hit);

    /**
     * The beam was disrupted by colliding with a block.
     * If the block is AIR, then `Beam.MAX_DISTANCE` was reached.
     * @param b Collided block
     */
    public void onBeamDisrupted(Block b, Location hit);
}
