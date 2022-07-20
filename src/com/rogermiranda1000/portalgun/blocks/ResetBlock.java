package com.rogermiranda1000.portalgun.blocks;

import org.bukkit.Location;

public class ResetBlock {
    private final Location position;

    protected ResetBlock(Location position) {
        this.position = position;
    }

    public Location getPosition() {
        return this.position;
    }
}
