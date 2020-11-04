package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class FloorPortal extends Portal {
    public FloorPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public void playParticle() {

    }

    public Location getTeleportLocation() {
        return this.position;
    }

    public Portal clone() {
        return new FloorPortal(this.position, this.direction, this.isLeft);
    }
}
