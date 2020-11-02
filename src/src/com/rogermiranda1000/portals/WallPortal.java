package com.rogermiranda1000.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class WallPortal extends Portal {
    WallPortal(Location loc, Direction dir, boolean isLeft) {
        super(loc, dir, isLeft);
    }

    public boolean insidePortal(Location loc) {
        return false;
    }

    public void playParticle() {

    }
}
