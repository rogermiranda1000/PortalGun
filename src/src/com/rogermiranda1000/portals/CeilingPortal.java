package com.rogermiranda1000.portals;

import com.rogermiranda1000.portalgun.Direction;
import org.bukkit.Location;

public class CeilingPortal extends Portal {
    CeilingPortal(Location loc, Direction dir) {
        super(loc, dir);
    }

    public boolean insidePortal(Location loc) {
        return false;
    }
}
