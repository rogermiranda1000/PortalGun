package com.rogermiranda1000.portalgun.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class FloorPortal extends Portal {
    FloorPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public boolean insidePortal(Location loc) {
        return false;
    }

    public void playParticle() {

    }

    public Location getTeleportLocation() {
        return this.position;
    }
}
