package com.rogermiranda1000.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class WallPortal extends Portal {
    WallPortal(Location loc, Direction dir) {
        super(loc, dir);
    }

    public boolean insidePortal(Location loc) {
        return false;
    }
}
